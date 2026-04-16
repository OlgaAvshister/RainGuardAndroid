package com.olga.avshister.rainguard.data.network.profile

import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.profile.Profile
import com.olga.avshister.rainguard.data.network.profile.Profile as NetworkProfile
import com.olga.avshister.rainguard.domain.profile.Role
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: Long,
    val phone: String,
    val name: String,
    val role: Role
) {
    companion object {
        fun NetworkProfile.toDomain(): Profile =
            Profile(
                id = id,
                name = name,
                phone = phone,
                role = role,
                cards = emptyList(),
                cart = Cart(products = arrayListOf()),
                activeRent = null,
            )
    }
}