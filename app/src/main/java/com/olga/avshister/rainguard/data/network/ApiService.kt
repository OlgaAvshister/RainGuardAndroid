package com.olga.avshister.rainguard.data.network

import com.olga.avshister.rainguard.data.network.auth.AuthRequest
import com.olga.avshister.rainguard.data.network.auth.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth")
    suspend fun auth(
        @Body request: AuthRequest
    ): Response<AuthResponse>
}