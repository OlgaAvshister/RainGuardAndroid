package com.olga.avshister.rainguard.data.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkClient(context: Context) {
    private var tokenManager: TokenManager = TokenManager(context)
    private val okHttpClient by lazy {
        val authInterceptor = AuthInterceptor(tokenManager)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .cache(null)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    // это IP, который указывал бы на сам эмулятор, если бы на нем развернули сервер
    // можно использовать для работы из Postman
    //private val baseUrl = "http://0.0.0.0:8080/"

    // такой сейчас IP на моей локальной машине
    private val baseLocalUrl = "http://192.168.1.91:8080/"
    private val baseRemoteUrl = "https://rain-guard-ktor-production-c84f.up.railway.app/"
    //private val baseUrl = "http://10.217.134.125:8080/"

    // а этот IP - для обращения эмулятора на сервер, развернутый локально на компьютере
    //private val baseUrl = "http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseRemoteUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
