package com.olga.avshister.rainguard.data.network.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val phone: String,
    val code: String,
)