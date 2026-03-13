package com.olga.avshister.rainguard.domain

import com.olga.avshister.rainguard.domain.products.Product

data class Checkout(
    val products: List<Product>,
    val rate: Rate,
) {

    enum class Rate(val textValue: String, val priceValue: Int) {
        PER_MINUTE ("Поминутный", 1),
        PER_HOUR ("Почасовой", 50),
        PER_DAY ("Посуточный", 350),
    }
}