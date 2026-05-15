package com.olga.avshister.rainguard.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.cart.CartLocalRepository
import com.olga.avshister.rainguard.data.cart.CartRepository
import com.olga.avshister.rainguard.data.cart.CheckoutLocalRepository
import com.olga.avshister.rainguard.data.cart.CheckoutRepository
import com.olga.avshister.rainguard.data.rent.RentRepository
import com.olga.avshister.rainguard.data.rent.RentRepositoryImpl
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.Checkout
import com.olga.avshister.rainguard.domain.rent.Rate
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectIdsViewModel(context: Context, val openToTake: Boolean, val rentPointId: Long): ViewModel() {
    private val rentPointRepository: RentPointRepository = RentPointRemoteRepository(context)

    private val rentRepository: RentRepository = RentRepositoryImpl(context)
    private val checkoutRepository: CheckoutRepository = CheckoutLocalRepository(context)
    private val cartRepository: CartRepository = CartLocalRepository

    private val _state = MutableStateFlow(
        State(
            suggestedIds = emptyList(),
            isLoading = false,
            error = null
        )
    )

    val state: StateFlow<State> = _state

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action.asSharedFlow()


    data class State(
        val suggestedIds: List<Long>,
        val isLoading: Boolean,
        val error: String? = null
    )

    sealed interface Intent {
        data class UpdateValue(val index: Int, val value: Long): Intent
        object ToCheckout: Intent
        object GiveToCheck: Intent
    }

    sealed class Action {
        data class OnNextState(val state: BSheetContentState) : Action()
    }

    class SelectIdsViewModelFactory(
        val context: Context,
        val openToTake: Boolean,
        val rentPointId: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SelectIdsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SelectIdsViewModel(context, openToTake, rentPointId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    init {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                // Предлагаем пользователю ids, чтобы ему самому не заполнять
                val initialIds = when (openToTake) {
                    true -> {
                        // инвентарные номера товаров из оформляемой сейчас аренды
                        getSuggestedIds(rentPointId = rentPointId)
                    }
                    false -> {
                        // инвентарные номера товаров из активной аренды
                        rentRepository.getActiveRent()?.productIds ?: emptyList()
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
        viewModelScope.launch {
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
                    val checkoutProducts = rentPointRepository.searchProducts(_state.value.suggestedIds, rentPointId)
                    checkoutRepository.setCheckout(Checkout(products = checkoutProducts, rate = Rate.PER_MINUTE))
                    _action.emit(Action.OnNextState(BSheetContentState.CheckoutState))
                }
                is Intent.GiveToCheck -> {
                    _action.emit(Action.OnNextState(BSheetContentState.GiveToCheckState))
                }
            }
        }
    }

    private suspend fun getSuggestedIds(rentPointId: Long): List<Long> = withContext(Dispatchers.IO) {
        // Нужно создать такую структуру, в которой
        // articul (10234) - quantity (2)
        //          ids =
        //          id = 1
        //          id = 2

        val suggestedIds = ArrayList<Long>()
        val items = cartRepository.getCart()?.products ?: emptyList()

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

        suggestedIds
    }
}