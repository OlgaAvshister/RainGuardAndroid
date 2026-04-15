package com.olga.avshister.rainguard.data.network

import com.olga.avshister.rainguard.data.network.auth.AuthRequest
import com.olga.avshister.rainguard.data.network.auth.AuthResponse
import com.olga.avshister.rainguard.data.network.profile.Profile
import com.olga.avshister.rainguard.data.network.rentPoint.RentPointNet
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth")
    suspend fun auth(
        @Body request: AuthRequest
    ): Response<AuthResponse>

    @GET("user")
    suspend fun getProfile(): Profile?

    @GET("rentPoints")
    suspend fun getRentPoints(): List<RentPointNet>
}