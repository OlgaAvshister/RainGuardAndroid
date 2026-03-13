package com.olga.avshister.rainguard.presentation.viewmodel.stuff

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.R
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.presentation.core.AUTH_PHONE_SCREEN
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckProductViewModel(application: Application) : AndroidViewModel(application) {
    val authRepository: AuthRepository = AuthLocalRepository(application)

    // Каналы для событий навигации
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    private val _screenState = MutableStateFlow(
        value = State(
            CheckProductState.FillProductId(
                id = 0,
                titleText = application.getString(R.string.fill_product_id_to_check),
                bottomButtonText = application.getString(R.string.next)
            )
        )
    )

    val screenState: StateFlow<State> = _screenState

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.ToCheckConditionClick -> {
                _screenState.value = State(
                    CheckProductState.SetProductCondition(
                        id = intent.id,
                        titleText = application.getString(R.string.what_product_condition_title),
                        bottomButtonText = application.getString(R.string.next_product_button)
                    )
                )
            }

            is Intent.ToFillProductId -> {
                _screenState.value = State(
                    checkProductState = CheckProductState.FillProductId(
                        id = 0,
                        titleText = application.getString(R.string.fill_product_id_to_check),
                        bottomButtonText = application.getString(R.string.next)
                    )
                )
            }

            is Intent.OnLogoutClick -> {
                authRepository.logout()
                viewModelScope.launch {
                    _navigationEvent.emit(NavigationEvent.NavigateToScreen(AUTH_PHONE_SCREEN))
                }
            }

            is Intent.OnConditionSelected -> {
                updateProductCondition(intent.id, intent.productCondition)
            }
        }

    }

    private fun updateProductCondition(id: Long, productCondition: Product.ProductCondition) {
        // обновляем на сервере состояние выбранного товара
    }

    data class State(
        val checkProductState: CheckProductState,
    )

    sealed interface Intent {
        object ToFillProductId : Intent
        data class ToCheckConditionClick(val id: Long) : Intent
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
            val id: Long,
            override val titleText: String,
            override val bottomButtonText: String
        ) : CheckProductState(
            titleText = titleText,
            bottomButtonText = bottomButtonText
        )

        data class SetProductCondition(
            val id: Long,
            override val titleText: String,
            override val bottomButtonText: String
        ) : CheckProductState(
            titleText = titleText,
            bottomButtonText = bottomButtonText
        )
    }

    sealed class NavigationEvent {
        data class NavigateToScreen(val route: String) : NavigationEvent()
        object NavigateBack : NavigationEvent()
    }
}