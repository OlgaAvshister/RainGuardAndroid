package com.olga.avshister.rainguard.domain.rent

data class Rent(
    val startedAt: Long, // время начала аренды в Unix-формате,
    val finishedAt: Long? = null, // время завершения аренды (если аренда уже завершена, оплачена),
    val startRentPointId: Long? = null,
    val finishRentPointId: Long? = null,
    val productIds: List<Long>,
    val cardNumber: String, // выбранная карта для последующей оплаты
    val rate: Rate, // выбранный тариф
)