package com.olga.avshister.rainguard.domain.cart


data class Cart(
    // список артикулов товаров, добавленных в корзину
    // (могут повторяться, если выбрано несколько штук одной позиции)
    val products: List<Long>,
)

