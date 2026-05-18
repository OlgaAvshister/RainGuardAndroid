package com.olga.avshister.rainguard.presentation.viewmodel.stuff

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.data.profile.AuthRemoteRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.presentation.core.AUTH_PHONE_SCREEN
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.Long
import kotlin.String

class CheckProductViewModel(application: Application) : AndroidViewModel(application) {
    val authRepository: AuthRepository = AuthRemoteRepository(application)
    val rentPointRepository: RentPointRepository = RentPointRemoteRepository(application)

    private val _event = MutableSharedFlow<Event>()
    val event: SharedFlow<Event> = _event

    private val _screenState = MutableStateFlow(
        value = State(
            CheckProductState.FillProductId(
                id = "",
                titleText = application.getString(R.string.fill_product_id_to_check),
                bottomButtonText = application.getString(R.string.next)
            )
        )
    )

    val screenState: StateFlow<State> = _screenState

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.OnIdUpdated -> {
                _screenState.value = State(
                    checkProductState = CheckProductState.FillProductId(
                        id = intent.id,
                        titleText = application.getString(R.string.fill_product_id_to_check),
                        bottomButtonText = application.getString(R.string.next)
                    )
                )
            }

            is Intent.OnNextButtonClicked -> {
                when(val state = _screenState.value.checkProductState) {
                    is CheckProductState.FillProductId -> {
                        viewModelScope.launch {
                            try {
                                checkProductCondition(state.id.toLong())?.let { condition ->
                                    _screenState.value = State(
                                        CheckProductState.SetProductCondition(
                                            id = state.id.toLong(),
                                            checkedProductCondition = condition,
                                            titleText = application.getString(R.string.what_product_condition_title),
                                            bottomButtonText = application.getString(R.string.next_product_button)
                                        )
                                    )
                                } ?: run {
                                    _event.emit(Event.Error("Ошибка получения товара с указанным id. Возможно, такого товара не существует"))
                                }
                            } catch (e: Exception) {
                                _event.emit(Event.Error("Не удалось проверить товар. Убедитесь, что номер правильный, и попробуйте еще раз"))
                            }
                        }
                    }
                    is CheckProductState.SetProductCondition -> {
                        viewModelScope.launch {
                            _screenState.value = State(
                                CheckProductState.FillProductId(
                                    id = "",
                                    titleText = application.getString(R.string.fill_product_id_to_check),
                                    bottomButtonText = application.getString(R.string.next)
                                )
                            )
                        }
                    }
                }
            }

            is Intent.OnLogoutClick -> {
                viewModelScope.launch {
                    authRepository.logout()
                    _event.emit(Event.NavigateToScreen(AUTH_PHONE_SCREEN))
                }
            }

            is Intent.OnConditionSelected -> {
                viewModelScope.launch {
                    updateProductCondition(intent.id, intent.productCondition)
                }
            }
        }
    }

    /**
     * Проверяем существование товара с таким id и заодно получаем его состояние
     */
    private suspend fun checkProductCondition(productId: Long): Product.ProductCondition? {
        val product = rentPointRepository.searchProducts(
            listOf(productId), rentPointId = null
        ).firstOrNull()
        return product?.condition
    }

    private suspend fun updateProductCondition(id: Long, productCondition: Product.ProductCondition) {
        rentPointRepository.updateCondition(productId = id, condition = productCondition)
        _screenState.value = _screenState.value.copy(
            checkProductState = (_screenState.value.checkProductState as CheckProductState.SetProductCondition).copy(
                id = id,
                checkedProductCondition = productCondition
            )
        )
    }

    data class State(
        val checkProductState: CheckProductState,
    )

    sealed interface Intent {
        data class OnIdUpdated(val id: String): Intent
        object OnNextButtonClicked: Intent
        object OnLogoutClick : Intent

        data class OnConditionSelected(
            val id: Long,
            val productCondition: Product.ProductCondition
        ) : Intent
    }


    sealed class CheckProductState(
        open val titleText: String,
        open val bottomButtonText: String
    ) {
        data class FillProductId(
            val id: String,
            override val titleText: String,
            override val bottomButtonText: String
        ) : CheckProductState(
            titleText = titleText,
            bottomButtonText = bottomButtonText
        )

        data class SetProductCondition(
            val id: Long,
            val checkedProductCondition: Product.ProductCondition,
            override val titleText: String,
            override val bottomButtonText: String,
        ) : CheckProductState(
            titleText = titleText,
            bottomButtonText = bottomButtonText
        )
    }

    sealed class Event {
        data class NavigateToScreen(val route: String) : Event()
        object NavigateBack : Event()
        data class Error(val message: String) : Event()
    }
}