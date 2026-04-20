package com.olga.avshister.rainguard.data.network.rent

import kotlinx.serialization.Serializable

@Serializable
data class ActiveRentResponse(
    val rent: RentNet?
)