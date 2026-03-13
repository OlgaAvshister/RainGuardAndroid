package com.olga.avshister.rainguard.domain.rent

import com.olga.avshister.rainguard.domain.products.Product

data class RentPoint(
    val id: Long,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val availableProducts: List<Product>,
)

