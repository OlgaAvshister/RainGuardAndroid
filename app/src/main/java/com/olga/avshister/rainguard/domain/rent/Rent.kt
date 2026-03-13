package com.olga.avshister.rainguard.domain.rent

import com.olga.avshister.rainguard.domain.Checkout
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.products.Product

data class Rent(
    val customerId: Long, // id клиента, который оформляет аренду
    val startedAt: Long, // время начала аренды в Unix-формате,
    val completedAt: Long? = null, // время завершения аренды (если аренда уже завершена, оплачена),
    val products: List<Product>,
    val selectedPaymentCard: Card, // выбранная карта для последующей оплаты
    val rate: Checkout.Rate, // выбранный тариф
)