package com.olga.avshister.rainguard.domain.rent

enum class Rate(val textValue: String, val priceValue: Int, val timeUnit: String) {
    PER_MINUTE ("Поминутный", priceValue = 1, timeUnit = "мин"),
    PER_HOUR ("Почасовой", priceValue = 50, timeUnit = "час"),
    PER_DAY ("Посуточный", priceValue = 350, timeUnit = "день"),
}