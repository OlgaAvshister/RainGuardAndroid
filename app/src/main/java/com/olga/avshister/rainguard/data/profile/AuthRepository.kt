package com.olga.avshister.rainguard.data.profile

import com.olga.avshister.rainguard.data.cart.CartRepository
import com.olga.avshister.rainguard.domain.profile.Profile

interface AuthRepository: CartRepository {
    fun auth(phone: String)
    suspend fun auth(phone: String, code: String): Profile

    suspend fun logout()

    suspend fun getProfile(): Profile?
    fun updateProfile(profile: Profile)

    fun finishRent(timeNow: Long)

    suspend fun registerStuff(name: String, phone: String)
}