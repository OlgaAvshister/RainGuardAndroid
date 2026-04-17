package com.olga.avshister.rainguard.data.network.card

import com.olga.avshister.rainguard.domain.payment.Card
import kotlinx.serialization.Serializable

interface CardContract {
    @Serializable
    data class CardNet(
        val number: String,
        val expired: String,
        val cvv: Int,
    ) {
        companion object {
            fun CardNet.toDomain(): Card {
                return Card(this.number, this.expired, this.cvv)
            }
        }
    }

    @Serializable
    data class AddCardRequest(
        val card: CardNet
    )
}