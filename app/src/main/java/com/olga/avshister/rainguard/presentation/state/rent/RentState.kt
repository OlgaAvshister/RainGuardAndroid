package com.olga.avshister.rainguard.presentation.state.rent

import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rate

data class RentState(
    val items: List<Product> = listOf(),
    val rentTime: String = "00:00:00",
    val cost: Int = 0,
    val rate: Rate = Rate.PER_MINUTE,
    val isLoading: Boolean = false,
    val error: String? = null
)