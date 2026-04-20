package com.olga.avshister.rainguard.data.rent

import com.olga.avshister.rainguard.domain.rent.Rent

interface RentRepository {
    suspend fun getActiveRent(): Rent?
}