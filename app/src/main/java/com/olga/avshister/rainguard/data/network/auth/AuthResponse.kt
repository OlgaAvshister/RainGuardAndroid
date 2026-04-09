package com.olga.avshister.rainguard.data.network.auth

import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.profile.Profile
import com.olga.avshister.rainguard.domain.profile.Role
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val user: User
) {
    @Serializable
    data class User(
        val id: Long,
        val phone: String,
        val name: String,
        val role: Role
    )

    companion object {
        fun AuthResponse.toDomain(): Profile = Profile(
            id = user.id,
            name = user.name,
            phone = user.phone,
            role = user.role,
            cards  = emptyList(),
            cart = Cart(products = listOf()),
            activeRent = null,
        )
    }
}