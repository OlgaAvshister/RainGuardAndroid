package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.olga.avshister.rainguard.data.cart.CheckoutLocalRepository
import com.olga.avshister.rainguard.data.cart.CheckoutRepository
import com.olga.avshister.rainguard.domain.Checkout
import com.olga.avshister.rainguard.domain.rent.Rate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val checkoutRepository: CheckoutRepository = CheckoutLocalRepository(application.applicationContext)

    private var checkout: Checkout = Checkout(emptyList(), Rate.PER_MINUTE)

    data class CheckoutUiState(
        val checkout: Checkout,
        val selectedRateIndex: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    init {
        val checkoutProducts = checkoutRepository.getCheckout().products
        checkout = checkout.copy(products = checkoutProducts)
    }
    private val _uiState = MutableStateFlow(
        CheckoutUiState(checkout = checkout)
    )

    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        // Инициализация индекса выбранного тарифа
        val rates = Rate.entries
        _uiState.update { currentState ->
            currentState.copy(
                selectedRateIndex = rates.indexOf(currentState.checkout.rate)
            )
        }
    }

    fun selectRate(index: Int) {
        val rates = Rate.entries
        if (index in rates.indices) {
            _uiState.update { currentState ->
                currentState.copy(
                    checkout = currentState.checkout.copy(
                        rate = rates[index]
                    ),
                    selectedRateIndex = index
                )
            }
        }
    }

    fun saveCheckout(rate: Rate) {
        checkoutRepository.setCheckout(checkout.copy(rate = rate))
    }
}