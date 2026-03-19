package com.olga.avshister.rainguard.domain

import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rate

data class Checkout(
    val products: List<Product>,
    val rate: Rate,
)