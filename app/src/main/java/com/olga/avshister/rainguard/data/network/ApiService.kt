package com.olga.avshister.rainguard.data.network

import com.olga.avshister.rainguard.data.network.auth.AuthRequest
import com.olga.avshister.rainguard.domain.profile.Profile
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth")
    suspend fun auth(
        @Body request: AuthRequest
    ): Profile
}