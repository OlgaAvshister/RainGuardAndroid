package com.olga.avshister.rainguard.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.cart.CheckoutLocalRepository
import com.olga.avshister.rainguard.data.cart.CheckoutRepository
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.Checkout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SelectIdsViewModel(context: Context, val openToTake: Boolean): ViewModel() {
    private val authRepository: AuthRepository = AuthLocalRepository(context)
    private val rentPointRepository: RentPointRepository = RentPointLocalRepository

    private val checkoutRepository: CheckoutRepository = CheckoutLocalRepository(context)

    class SelectIdsViewModelFactory(
        val context: Context,
        val openToTake: Boolean
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SelectIdsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SelectIdsViewModel(context, openToTake) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    data class State(
        val suggestedIds: List<Long>,
        val isLoading: Boolean,
        val error: String? = null
    )

    private val _state = MutableStateFlow(
        State(
            suggestedIds = emptyList(),
            isLoading = false,
            error = null
        )
    )

    val state: StateFlow<State> = _state

    sealed interface Intent {
        data class UpdateValue(val index: Int, val value: Long): Intent
        object ToCheckout: Intent
        object GiveToCheck: Intent
    }

    init {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                // Предлагаем пользователю ids, чтобы ему самому не заполнять
                val initialIds = when (openToTake) {
                    true -> {
                        // инвентарные номера товаров из оформлемой сейчас аренды
                        getSuggestedIds(rentPointId = authRepository.getCurrentRentPointId())
                    }
                    false -> {
                        // инвентарные номера товаров из активной аренды
                        authRepository.getProfile()?.activeRent?.products?.map { it.id } ?: emptyList()
                    }
                }
                _state.update { it.copy(
                    isLoading = false,
                    suggestedIds = initialIds
                ) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Ошибка получения инвентарных номеров товаров",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.UpdateValue -> {
                _state.update {
                    val suggestedIdsModified = it.suggestedIds.toMutableList().apply {
                        this[intent.index] = intent.value
                    }
                    it.copy(suggestedIds = suggestedIdsModified.toList())
                }
            }
            is Intent.ToCheckout -> {
                val checkoutProducts = rentPointRepository.searchProducts(_state.value.suggestedIds, authRepository.getCurrentRentPointId())
                checkoutRepository.setCheckout(Checkout(products = checkoutProducts, rate = Checkout.Rate.PER_MINUTE))
            }
            is Intent.GiveToCheck -> {
                // todo: запомнить и затем перейти на след экран
            }
        }
    }

    private fun getSuggestedIds(rentPointId: Long): List<Long> {
        // Нужно создать такую структуру, в которой
        // articul (10234) - quantity (2)
        //          ids =
        //          id = 1
        //          id = 2

        val suggestedIds = ArrayList<Long>()
        val items = authRepository.getCart()?.products ?: emptyList()

        /**
         * Здесь мы берем список артикулов и трансформируем его в список
         * article - count (количество товаров в списке с таким артикулом)
         */
        val pairs: List<Pair<Long, Int>> = items
            .groupingBy { it }
            .eachCount()
            .toList()

        pairs.forEach { pair ->
            // first - артикул, second - количество запрашиваемых товаров с указанным артикулом
            // наполняем список инвентарных номеров в suggestedIds, чтобы предзаполнить данные для пользователя
            suggestedIds.addAll(
                rentPointRepository
                    .searchProducts(pair.first, rentPointId)
                    .take(pair.second)
                    // берем список товаров и преобразуем в список их артикулов
                    .map { it.id }
            )
        }

        return suggestedIds
    }
}