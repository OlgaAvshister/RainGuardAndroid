package com.olga.avshister.rainguard.domain.payment

data class Card(
    val number: String,
    val expired: String, // нужно будет переделать формат 04/26
    val cvv: Int,
)