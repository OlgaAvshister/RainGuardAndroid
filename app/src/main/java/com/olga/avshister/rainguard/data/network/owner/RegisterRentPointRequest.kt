package com.olga.avshister.rainguard.data.network.owner

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRentPointRequest(
    val name: String,
    val fullAddress: String,
    val latitude: Double,
    val longitude: Double,
    val workHours: String,
)