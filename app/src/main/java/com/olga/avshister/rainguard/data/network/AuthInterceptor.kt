package com.olga.avshister.rainguard.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        // Если токена нет, пропускаем запрос без изменений
        if (token.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val newRequest = originalRequest.newBuilder()
            .addHeader(TOKEN_HEADER, token)
            .build()

        return chain.proceed(newRequest)
    }

    companion object {
        const val TOKEN_HEADER = "X-Auth-Token"
    }
}