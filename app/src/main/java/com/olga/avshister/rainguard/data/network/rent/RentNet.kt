package com.olga.avshister.rainguard.data.network.rent

import com.olga.avshister.rainguard.domain.rent.Rate
import com.olga.avshister.rainguard.domain.rent.Rent

data class RentNet(
    val startedAt: Long, // время начала аренды в Unix-формате,
    val finishedAt: Long? = null, // время завершения аренды (если аренда уже завершена, оплачена),
    val startRentPointId: Long, // id точки аренды
    val finishRentPointId: Long? = null, // id точки возврата
    val productIds: List<Long>, // id товаров, взятых в аренду,
    val cardNumber: String, // номер выбранной карты для последующей оплаты
    val rate: Rate, // выбранный тариф
) {
    companion object {
        fun RentNet.toDomain(): Rent {
            return Rent(
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