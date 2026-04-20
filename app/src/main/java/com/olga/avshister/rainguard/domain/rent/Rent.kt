package com.olga.avshister.rainguard.domain.rent

import com.olga.avshister.rainguard.data.network.rent.RentNet

data class Rent(
    val startedAt: Long, // время начала аренды в Unix-формате,
    val finishedAt: Long? = null, // время завершения аренды (если аренда уже завершена, оплачена),
    val startRentPointId: Long,
    val finishRentPointId: Long? = null,
    val productIds: List<Long>,
    val cardNumber: String, // выбранная карта для последующей оплаты
    val rate: Rate, // выбранный тариф
) {
    companion object {
        fun Rent.toNet(): RentNet {
            return RentNet(
                startedAt = startedAt,
                finishedAt = finishedAt,
                startRentPointId = startRentPointId,
                finishRentPointId = finishRentPointId,
                productIds = productIds,
                cardNumber = cardNumber,
                rate = rate,
            )
        }
    }
}