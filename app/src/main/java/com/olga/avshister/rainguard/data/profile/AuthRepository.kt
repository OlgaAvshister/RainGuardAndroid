package com.olga.avshister.rainguard.data.profile

import com.olga.avshister.rainguard.domain.profile.Profile

interface AuthRepository {
    suspend fun auth(phone: String, code: String): Profile

    suspend fun logout()

    suspend fun getProfile(): Profile?
    suspend fun registerStuff(name: String, phone: String)
}