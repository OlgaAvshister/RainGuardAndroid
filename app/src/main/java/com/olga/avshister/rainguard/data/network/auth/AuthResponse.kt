package com.olga.avshister.rainguard.data.network.auth

import com.olga.avshister.rainguard.data.network.profile.ProfileNet as NetworkProfile
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val user: NetworkProfile)