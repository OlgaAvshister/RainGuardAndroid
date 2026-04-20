package com.olga.avshister.rainguard.data.network

import com.olga.avshister.rainguard.data.network.auth.AuthRequest
import com.olga.avshister.rainguard.data.network.auth.AuthResponse
import com.olga.avshister.rainguard.data.network.card.CardContract
import com.olga.avshister.rainguard.data.network.profile.ProfileNet
import com.olga.avshister.rainguard.data.network.rent.ActiveRentResponse
import com.olga.avshister.rainguard.data.network.rentPoint.RentPointNet
import com.olga.avshister.rainguard.data.network.rentPoint.StartRentRequest
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
    suspend fun getProfile(): ProfileNet?

    @GET("rentPoints")
    suspend fun getRentPoints(): List<RentPointNet>

    @POST("addCard")
    suspend fun addCard(
        @Body request: CardContract.AddCardRequest
    )

    @GET("getCards")
    suspend fun getCards(): List<CardContract.CardNet>

    @POST("startRent")
    suspend fun startRent(@Body request: StartRentRequest)

    @GET("activeRent")
    suspend fun getActiveRent(): ActiveRentResponse
}