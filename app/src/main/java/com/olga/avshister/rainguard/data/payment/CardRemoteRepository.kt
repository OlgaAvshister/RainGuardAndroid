package com.olga.avshister.rainguard.data.payment

import android.content.Context
import com.olga.avshister.rainguard.data.network.NetworkClient
import com.olga.avshister.rainguard.data.network.card.CardContract
import com.olga.avshister.rainguard.data.network.card.CardContract.CardNet.Companion.toDomain
import com.olga.avshister.rainguard.domain.payment.Card

class CardRemoteRepository(val context: Context): CardRepository {
    override suspend fun addCard(card: Card) {
        val request = CardContract.AddCardRequest(
            CardContract.CardNet(
                number = card.number,
                expired = card.expired,
                cvv = card.cvv
            )
        )
        NetworkClient(context).apiService.addCard(request)
    }

    override suspend fun getCards(): List<Card> {
        return NetworkClient(context)
            .apiService
            .getCards()
            .map() { it.toDomain() }
    }
}