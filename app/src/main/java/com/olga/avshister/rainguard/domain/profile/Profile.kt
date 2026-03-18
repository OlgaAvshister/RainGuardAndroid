package com.olga.avshister.rainguard.domain.profile

import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.rent.Rent

data class Profile(
    val id: Long,
    val name: String?,
    val phone: String,
    val role: Role,
    val cart: Cart,
    val cards: List<Card>,
    val activeRent: Rent?,
)