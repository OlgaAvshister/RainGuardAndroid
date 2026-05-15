package com.olga.avshister.rainguard.data.profile

import android.content.Context
import com.olga.avshister.rainguard.data.network.AuthInterceptor.Companion.TOKEN_HEADER
import com.olga.avshister.rainguard.data.network.NetworkClient
import com.olga.avshister.rainguard.data.network.TokenManager
import com.olga.avshister.rainguard.data.network.auth.AuthRequest
import com.olga.avshister.rainguard.data.network.owner.RegisterStuffRequest
import com.olga.avshister.rainguard.data.network.profile.ProfileNet.Companion.toDomain
import com.olga.avshister.rainguard.domain.profile.Profile
import com.olga.avshister.rainguard.domain.profile.Role

class AuthRemoteRepository(val context: Context): AuthRepository {
    val apiService = NetworkClient(context).apiService

    override suspend fun auth(
        phone: String,
        code: String
    ): Profile {
        val request = AuthRequest(phone, code)

        // Выполняем синхронный запрос (для корутин)
        val response = apiService.auth(request)
        val token = response.headers()[TOKEN_HEADER]
        TokenManager(context).saveToken(token.orEmpty())

        return response.body()?.user!!.toDomain()
    }

    override suspend fun logout() {
        TokenManager(context).clearToken()
    }

    override suspend fun getProfile(): Profile? {
        return apiService.getProfile()?.toDomain()
    }

    override suspend fun registerStuff(name: String, phone: String) {
        apiService.registerStuff(RegisterStuffRequest(name, phone, Role.STUFF.name))
    }
}