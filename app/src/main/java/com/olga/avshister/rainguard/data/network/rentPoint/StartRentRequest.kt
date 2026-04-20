package com.olga.avshister.rainguard.data.network.rentPoint

import com.olga.avshister.rainguard.domain.rent.Rate
import kotlinx.serialization.Serializable

@Serializable
data class StartRentRequest(
    val startRentPointId: Long,
    val startedAt: Long, // время начала аренды в Unix-формате,
    val productIds: List<Long>,
    val cardNumber: String, // выбранная карта для последующей оплаты
    val rate: Rate, // выбранный тариф
)