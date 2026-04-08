package com.olga.avshister.rainguard.data.profile

import android.content.Context
import com.olga.avshister.rainguard.data.network.NetworkClient.apiService
import com.olga.avshister.rainguard.data.network.auth.AuthRequest
import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.profile.Profile

class AuthRemoteRepository(context: Context): AuthRepository {
    override fun auth(phone: String) {
        TODO("Not yet implemented")
    }

    override suspend fun auth(
        phone: String,
        code: String
    ): Profile {
        return apiService.auth(AuthRequest(phone, code))
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun getProfile(): Profile? {
        //TODO("Not yet implemented")
        return null
    }

    override fun updateProfile(profile: Profile) {
        TODO("Not yet implemented")
    }

    override fun updateCart(cart: Cart) {
        TODO("Not yet implemented")
    }

    override suspend fun addCard(card: Card) {
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