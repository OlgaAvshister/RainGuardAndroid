package com.olga.avshister.rainguard.data.profile

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.olga.avshister.rainguard.data.common.PrefsRepository
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl
import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.profile.Profile
import com.olga.avshister.rainguard.domain.profile.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

class AuthLocalRepository(context: Context): AuthRepository {
    private val prefs: PrefsRepository = PrefsRepositoryImpl(context)

    override fun auth(phone: String) {
        sendSmsStub(phone)
    }

    override suspend fun auth(phone: String, code: String): Profile {
        if (isCodeValid(code)) {
            val profile = if (isUserExist(phone)) {
                authUser(phone)
            } else {
                registerAndAuthUser(phone)
            }
            return profile
        } else {
            throw IllegalArgumentException("Неверный код из SMS")
        }
    }

    override suspend fun logout() {
        delay(100)
        prefs.setLong(KEY_CURRENT_PROFILE_ID, value = -1)
        prefs.setBoolean(KEY_IS_USER_LOGGED, false)
    }

    override suspend fun getProfile(): Profile? = withContext(Dispatchers.IO) {
        // Имитация загрузки данных (для проверки прогрессбаров и пр.)
        delay(200)
        val currentProfileId = prefs.getLong(KEY_CURRENT_PROFILE_ID, -1)
        getAllProfiles().find { it.id == currentProfileId }
    }

    override fun updateProfile(profile: Profile) {
        val initialProfileList = getAllProfiles()
        var isListModified = false
        val modifiedList = initialProfileList.toMutableList().apply {
            forEachIndexed { index, currentProfile ->
                currentProfile.phone == profile.phone
                this[index] = profile
                isListModified = true
            }
        }

        if (isListModified.not()) {
            modifiedList.add(profile)
        }

        val json = Gson().toJson(modifiedList.toList())
        prefs.setString(KEY_ALL_PROFILES, json)
    }

    override fun finishRent(timeNow: Long) {
        // todo:
        /**
         * Нужно сделать запись о завершенной аренде:
         * 1. Добавить в таблицу прибыль,
         * 2. Выбросить товары в пункте выдачи (сделать их доступными для новой аренды)
         */
    }

    override suspend fun getCart(): Cart? {
        return getProfile()?.cart
    }

    override suspend fun addToCart(article: Long) {
        getProfile()?.let { profile ->
            val modifiedCartProducts = getCart()?.products?.toMutableList()
            modifiedCartProducts?.let { products ->
                products.add(article)
                    updateProfile(
                        profile.copy(
                            cart = Cart(ArrayList(products))
                        )
                    )
            } ?: NullPointerException("AuthLocalRepository/addToCart: Список для обновления корзины null")

        } ?: throw NullPointerException ("AuthLocalRepository/addToCart: Не удалось получить профиль null")
    }

    override suspend fun removeFromCart(article: Long) {
        getProfile()?.let { profile ->
            val modifiedCartProducts = getCart()?.products?.toMutableList()
            modifiedCartProducts?.let { products ->
                products.remove(article)
                updateProfile(
                    profile.copy(
                        cart = Cart(ArrayList(products))
                    )
                )
            }
        } ?: throw NullPointerException ("AuthLocalRepository/removeFromCart: Не удалось получить профиль null")
    }

    override suspend fun clearCart() {
        // Очщаем корзину сразу после формирования заказа
        getProfile()?.let { profile ->
            updateProfile(
                profile.copy(
                    cart = Cart(arrayListOf())
                )
            )
        }
    }

    override fun getCurrentRentPointId(): Long {
        return prefs.getLong(KEY_CURRENT_RENT_POINT_ID)
    }

    override fun setCurrentRentPointId(id: Long) {
        return prefs.setLong(KEY_CURRENT_RENT_POINT_ID, id)
    }

    override fun addStuff(name: String, phone: String) {
        registerUser(name = name, phone = phone, role = Role.STUFF)
    }

    private fun isUserExist(phone: String): Boolean {
        return getAllProfiles().find { it.phone == phone }?.let { true } ?: false
    }

    private fun isCodeValid(code: String): Boolean {
        return code == VALID_CODE
    }

    private fun getAllProfiles(): List<Profile> {
        val json = prefs.getString(KEY_ALL_PROFILES, "[]")
        val listType = object : TypeToken<List<Profile>>() {}.type
        val profiles: List<Profile> = Gson().fromJson(json, listType)
        return profiles
    }

    private fun authUser(phone: String): Profile {
        val profile = getAllProfiles().find { it.phone == phone }!!
        prefs.setLong(KEY_CURRENT_PROFILE_ID, profile.id)
        prefs.setBoolean(KEY_IS_USER_LOGGED, true)
        return profile
    }

    private fun registerAndAuthUser(phone: String): Profile {
        Log.d("registerAndAuthUser", "phone=$phone")
        registerUser(phone = phone)
        return authUser(phone)
    }

    private fun registerUser(name: String? = null, phone: String, role: Role? = null) {
        val createdProfile = Profile(
            id = Random.nextLong(),
            name = name,
            phone = phone,
            role = role ?: getMockProfileRole(phone),
            cart = Cart(products = arrayListOf()),
            cards = emptyList(),
            activeRent = null,
        )
        updateProfile(createdProfile)
    }

    private fun sendSmsStub(phone: String) {
        // заглушка отправки SMS
    }

    // todo: при работе с удаленным сервером уже будет задана роль при авторизации (предзаполненный профиль)
    // или добавление Stuff будет происходить через ЛК Owner'a
    private fun getMockProfileRole(phone: String): Role {
        val role = when (phone) {
            MOCK_STUFF_NUMBER -> Role.STUFF
            MOCK_OWNER_NUMBER -> Role.OWNER
            else -> Role.CUSTOMER
        }
        Log.d("registerAndAuthUser", "getMockProfileRole called, phone=$phone, role=$role")
        return role
    }

    companion object {
        private const val MOCK_STUFF_NUMBER = "1111111111"
        private const val MOCK_OWNER_NUMBER = "2222222222"
        private const val VALID_CODE = "1234"
        private const val KEY_PROFILE = "KEY_PROFILE"
        private const val KEY_ALL_PROFILES = "KEY_ALL_PROFILES"
        private const val KEY_IS_USER_LOGGED = "KEY_IS_USER_LOGGED"
        private const val KEY_CURRENT_PROFILE_ID = "KEY_CURRENT_PROFILE_ID"
        private const val KEY_CURRENT_RENT_POINT_ID = "KEY_CURRENT_RENT_POINT_ID"
    }
}