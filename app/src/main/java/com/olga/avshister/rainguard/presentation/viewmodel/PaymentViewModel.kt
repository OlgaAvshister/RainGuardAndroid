package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.cart.CheckoutLocalRepository
import com.olga.avshister.rainguard.data.cart.CheckoutRepository
import com.olga.avshister.rainguard.data.payment.CardRemoteRepository
import com.olga.avshister.rainguard.data.payment.CardRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.rent.Rent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PaymentViewModel(application: Application): AndroidViewModel(application) {
    private val cardRepository: CardRepository = CardRemoteRepository(application)

    private val rentPointRepository: RentPointRepository = RentPointRemoteRepository(application)

    private val checkoutRepository: CheckoutRepository = CheckoutLocalRepository(application)

    sealed class PaymentUiState {
        object Select : PaymentUiState()
        object Add : PaymentUiState()
        object Success : PaymentUiState()
    }

        var uiState by mutableStateOf<PaymentUiState>(PaymentUiState.Select)
            private set

        var cards by mutableStateOf<List<Card>>(emptyList())
            private set

        var selectedCard by mutableStateOf<Card?>(null)
            private set

        init {
            viewModelScope.launch {
                loadCards()
            }
        }


    private suspend fun loadCards() = withContext(Dispatchers.IO) {
      cards = cardRepository.getCards()
      selectedCard = cards.firstOrNull()
    }

    fun selectCard(card: Card) {
        selectedCard = card
    }

    fun goToAdd() {
        uiState = PaymentUiState.Add
    }

    fun addCard(number: String, expired: String, cvv: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newCard = Card(
                    number = maskCardNumber(number),
                    expired = "**/**",
                    cvv = getCvvHash(cvv.toInt())
                )

                cardRepository.addCard(newCard)
                loadCards()
            }
            withContext(Dispatchers.Main) {
                uiState = PaymentUiState.Success
            }
        }
    }

    fun confirmSelection() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val checkout = checkoutRepository.getCheckout()

                selectedCard?.let { card ->
                    rentPointRepository.startRent(
                        Rent(
                            startRentPointId = rentPointRepository.getStartRentPointId(),
                            startedAt = System.currentTimeMillis(),
                            productIds = checkout.products.map { it.id },
                            rate = checkout.rate,
                            cardNumber = card.number,
                        )
                    )
                }
            }
        }
    }

    fun backToSelect() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loadCards()
            }
            withContext(Dispatchers.Main) {
                uiState = PaymentUiState.Select
            }
        }
    }

    fun onBack() {
        uiState = when (uiState) {
            PaymentUiState.Select -> PaymentUiState.Select
            PaymentUiState.Add -> PaymentUiState.Select
            PaymentUiState.Success -> PaymentUiState.Select
        }
    }

    private fun getCvvHash(cvv: Int): Int {
        return (cvv * 7 + 13) % 10000
    }

    private fun maskCardNumber(number: String): String {
        return "*" + number.takeLast(4)
    }
}