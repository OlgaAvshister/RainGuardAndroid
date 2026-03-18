package com.olga.avshister.rainguard.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.filter.Filter
import com.olga.avshister.rainguard.domain.products.Product.*
import com.olga.avshister.rainguard.domain.profile.Profile
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.state.rent.RentState
import com.olga.avshister.rainguard.presentation.ui.utils.Utils
import com.olga.avshister.rainguard.presentation.viewmodel.CurrentRentViewModel.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.Long

class CustomerRentPointViewModel(
    private val context: Context,
    private val rentPoint: RentPoint,
) : ViewModel() {

    private val authRepository: AuthRepository = AuthLocalRepository(context)

    private var timerJob: Job? = null
    private var profile: Profile? = null
    private val activeRent: Rent? by lazy { profile?.activeRent }

    private var startTime = activeRent?.startedAt

    data class FilterState(
        val filter: Filter
    )

    private val _rentState = MutableStateFlow(RentState(isLoading = true))
    val rentState: StateFlow<RentState> = _rentState.asStateFlow()

    private val _filterState = MutableStateFlow(
        FilterState(
            filter = Filter(
                productType = null,
                printType = null,
                color = null,
                formFactor = null,
                size = null
            )
        )
    )

    val filterState: StateFlow<FilterState> = _filterState

    init {
        Log.d("CUSTOMER_RENT_POINT_VM", "init")
        authRepository.setCurrentRentPointId(rentPoint.id)
        viewModelScope.async(Dispatchers.IO) {
            authRepository.getProfile()
        }
        startTimer()
    }

    fun hasActiveRent(): Boolean = profile?.activeRent != null

    // ---------- Intents ----------

    sealed interface Intent {
        data class SelectProductType(val type: ProductType) : Intent
        data class SelectFormFactor(val formFactor: FormFactor) : Intent
        data class SelectPrintType(val printType: PrintType) : Intent
        data class SelectSize(val size: Size) : Intent
        data class UpdateTime(val timeInMillis: Long) : Intent
    }

    fun onIntent(intent: Intent) {
        Log.d("CUSTOMER_RENT_POINT_VM", "onIntent: $intent")
        when (intent) {
            is Intent.SelectProductType -> {
                _filterState.update {
                    it.copy(
                        filter = it.filter.copy(
                            productType = intent.type,
                            formFactor = null,
                            size = null
                        )
                    )
                }
            }

            is Intent.SelectFormFactor -> {
                _filterState.update {
                    it.copy(
                        filter = it.filter.copy(
                            formFactor = intent.formFactor
                        )
                    )
                }
            }

            is Intent.SelectPrintType -> {
                _filterState.update {
                    it.copy(
                        filter = it.filter.copy(
                            printType = intent.printType
                        )
                    )
                }
            }

            is Intent.SelectSize -> {
                _filterState.update {
                    it.copy(
                        filter = it.filter.copy(
                            size = intent.size
                        )
                    )
                }
            }

            is Intent.StartTimer -> {
                startTimer()
            }

            is Intent.UpdateTime -> {
                updateTime(intent.timeInMillis)
            }
        }
    }

    private fun startTimer() {
        Log.d("CUSTOMER_RENT_POINT_VM", "startTimer")
        stopTimer()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d("CUSTOMER_RENT_POINT_VM", "startTimer, insideLaunch")
            while (true) {
                startTime?.let {
                    val elapsedTime = System.currentTimeMillis() - it
                    onIntent(Intent.UpdateTime(elapsedTime))
                    delay(1000) // Обновляем каждую секунду
                }
            }
        }
    }

    private fun stopTimer() {
        Log.d("CUSTOMER_RENT_POINT_VM", "startTimer")
        timerJob?.cancel()
        timerJob = null
    }

    private fun updateTime(timeInMillis: Long) {
        activeRent?.let { rent ->
            _rentState.value = _rentState.value.copy(
                rentTime = Utils.millisToHumanTime(timeInMillis),
                cost = Utils.calculateCost(
                    timeInMillis = timeInMillis,
                    rate = rent.rate,
                    productsCount = rent.products.size),
                items = rent.products,
            )
        }
    }

    // ---------- Factory ----------

    class RentPointViewModelFactory(
        private val context: Context,
        private val rentPoint: RentPoint,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CustomerRentPointViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CustomerRentPointViewModel(context, rentPoint) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}