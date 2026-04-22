package com.olga.avshister.rainguard.data.network.owner

import kotlinx.serialization.Serializable

@Serializable
data class RegisterStuffRequest(
    val name: String,
    val phone: String,
    val role: String
)