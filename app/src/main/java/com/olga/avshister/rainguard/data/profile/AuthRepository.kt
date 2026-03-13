package com.olga.avshister.rainguard.data.profile

import com.olga.avshister.rainguard.data.cart.CartRepository
import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.profile.Profile

interface AuthRepository: CartRepository {
    fun auth(phone: String)
    fun auth(phone: String, code: String): Profile

    fun logout()

    fun getProfile(): Profile?
    fun updateProfile(profile: Profile)

    fun updateCart(cart: Cart)
    fun addCard(card: Card)

    fun finishRent(timeNow: Long)
}