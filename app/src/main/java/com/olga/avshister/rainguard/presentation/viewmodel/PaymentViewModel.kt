package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.olga.avshister.rainguard.data.cart.CheckoutLocalRepository
import com.olga.avshister.rainguard.data.cart.CheckoutRepository
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.cart.Cart
import com.olga.avshister.rainguard.domain.payment.Card
import com.olga.avshister.rainguard.domain.rent.Rent


class PaymentViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository: AuthRepository = AuthLocalRepository(application)
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
            loadCards()
        }


    private fun loadCards() {
      val profile = authRepository.getProfile()!!
      cards = profile.cards
      selectedCard = cards.firstOrNull()
    }

    fun selectCard(card: Card) {
        selectedCard = card
    }

    fun goToAdd() {
        uiState = PaymentUiState.Add
    }

    fun addCard(number: String, expired: String, cvv: String) {
        val newCard = Card(
            number = number,
            expired = expired,
            cvv = cvv.toInt()
        )

        authRepository.addCard(newCard)

        loadCards()
        uiState = PaymentUiState.Success
    }

    fun confirmSelection(/*navController: NavController*/) {
        val profile = authRepository.getProfile()!!
        val checkoutProducts = checkoutRepository.getCheckout().products

        selectedCard?.let { card ->
            authRepository.updateProfile(
                profile.copy(
                    cart = Cart(emptyList()), // обнуляем корзину (подготовка для следующего заказа)
                    activeRent = Rent(
                        customerId = profile.id,
                        startedAt = System.currentTimeMillis(),
                        products = checkoutProducts,
                        rate = checkoutRepository.getCheckout().rate,
                        selectedPaymentCard = card
                    )
                )
            )
        }
    }

    fun backToSelect() {
        uiState = PaymentUiState.Select
        loadCards()
    }

    fun onBack() {
        uiState = when (uiState) {
            PaymentUiState.Select -> PaymentUiState.Select
            PaymentUiState.Add -> PaymentUiState.Select
            PaymentUiState.Success -> PaymentUiState.Select
        }
    }
}