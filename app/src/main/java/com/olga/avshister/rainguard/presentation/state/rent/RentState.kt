package com.olga.avshister.rainguard.presentation.state.rent

import com.olga.avshister.rainguard.domain.Checkout
import com.olga.avshister.rainguard.domain.products.Product

data class RentState(
    val items: List<Product> = listOf(),
    val rentTime: String = "00:00:00",
    val cost: Int = 0,
    val rate: Checkout.Rate = Checkout.Rate.PER_MINUTE,
    val isLoading: Boolean = false,
    val error: String? = null
)