package com.olga.avshister.rainguard.data.profile

import android.content.Context
import com.olga.avshister.rainguard.data.network.AuthInterceptor.Companion.TOKEN_HEADER
import com.olga.avshister.rainguard.data.network.NetworkClient
import com.olga.avshister.rainguard.data.network.TokenManager
import com.olga.avshister.rainguard.data.network.auth.AuthRequest
import com.olga.avshister.rainguard.data.network.profile.ProfileNet.Companion.toDomain
import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.profile.Profile

class AuthRemoteRepository(val context: Context): AuthRepository {
    override fun auth(phone: String) {
        TODO("Not yet implemented")
    }

    override suspend fun auth(
        phone: String,
        code: String
    ): Profile {
        val request = AuthRequest(phone, code)

        // Выполняем синхронный запрос (для корутин)
        val response = NetworkClient(context).apiService.auth(request)
        val token = response.headers()[TOKEN_HEADER]
        TokenManager(context).saveToken(token.orEmpty())

        return response.body()?.user!!.toDomain()
    }

    override suspend fun logout() {
        TokenManager(context).clearToken()
    }

    override suspend fun getProfile(): Profile? {
        return NetworkClient(context).apiService.getProfile()?.toDomain()
    }

    override fun updateProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override fun finishRent(timeNow: Long) {
        TODO("Not yet implemented")
    }

    override fun addStuff(name: String, phone: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getCart(): Cart? {
        TODO("Not yet implemented")
    }

    override suspend fun addToCart(article: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun removeFromCart(article: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCart() {
        TODO("Not yet implemented")
    }

    override fun getCurrentRentPointId(): Long {
        TODO("Not yet implemented")
    }

    override fun setCurrentRentPointId(id: Long) {
        TODO("Not yet implemented")
    }
}