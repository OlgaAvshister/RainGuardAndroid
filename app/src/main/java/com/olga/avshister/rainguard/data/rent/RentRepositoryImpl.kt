package com.olga.avshister.rainguard.data.rent

import android.content.Context
import com.olga.avshister.rainguard.data.network.NetworkClient
import com.olga.avshister.rainguard.data.network.rent.RentNet.Companion.toDomain
import com.olga.avshister.rainguard.domain.rent.Rent

class RentRepositoryImpl(context: Context): RentRepository {
    val apiService = NetworkClient(context).apiService
    override suspend fun getActiveRent(): Rent? {
        return apiService.getActiveRent().rent?.toDomain()
    }
}