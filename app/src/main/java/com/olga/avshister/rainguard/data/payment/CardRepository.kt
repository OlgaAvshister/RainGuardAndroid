package com.olga.avshister.rainguard.data.payment

import com.olga.avshister.rainguard.domain.payment.Card

interface CardRepository {
    suspend fun addCard(card: Card)
    suspend fun getCards(): List<Card>
}