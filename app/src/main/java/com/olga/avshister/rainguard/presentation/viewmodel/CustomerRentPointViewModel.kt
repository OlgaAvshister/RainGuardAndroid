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
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.state.rent.RentState
import com.olga.avshister.rainguard.presentation.ui.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _customerRentPointState = MutableStateFlow(
        CustomerRentPointState(
            loadingState = true,
            filterState = Filter(
                productType = null,
                printType = null,
                color = null,
                formFactor = null,
                size = null
            ),
            rentState = RentState(isLoading = true)
        )
    )

    val customerRentPointState = _customerRentPointState.asStateFlow()

    data class CustomerRentPointState(
        val loadingState: Boolean,
        val filterState: Filter,
        val rentState: RentState
    )

    // ---------- Intents ----------

    sealed interface Intent {
        object LoadData: Intent
        data class SelectProductType(val type: ProductType) : Intent
        data class SelectFormFactor(val formFactor: FormFactor) : Intent
        data class SelectPrintType(val printType: PrintType) : Intent
        data class SelectSize(val size: Size) : Intent
    }

    fun onIntent(intent: Intent) {
        Log.d("CUSTOMER_RENT_POINT_VM", "onIntent: $intent")
        when (intent) {
            is Intent.LoadData -> {
                authRepository.setCurrentRentPointId(rentPoint.id)
                loadProfile()
            }
            is Intent.SelectProductType -> {
                _customerRentPointState.update {
                    it.copy(
                        filterState = it.filterState.copy(
                            productType = intent.type,
                            formFactor = null,
                            size = null
                        )
                    )
                }
            }

            is Intent.SelectFormFactor -> {
                _customerRentPointState.update {
                    it.copy(
                        filterState = it.filterState.copy(
                            formFactor = intent.formFactor
                        )
                    )
                }
            }

            is Intent.SelectPrintType -> {
                _customerRentPointState.update {
                    it.copy(
                        filterState = it.filterState.copy(
                            printType = intent.printType
                        )
                    )
                }
            }

            is Intent.SelectSize -> {
                _customerRentPointState.update {
                    it.copy(
                        filterState = it.filterState.copy(
                            size = intent.size
                        )
                    )
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profile = authRepository.getProfile()
            profile?.activeRent?.let { rent ->
                _customerRentPointState.update {
                    it.copy(
                        loadingState = false,
                        rentState = RentState(
                            isLoading = false,
                            items = rent.products,
                            rate = rent.rate,
                        )
                    )
                }
                startTimer()
            } ?: run {
                // если активной аренды нет, то просто создаем RentState со значениями по умолчанию
                // для запуска экрана с фильтрами
                _customerRentPointState.update {
                    it.copy(
                        loadingState = false,
                        rentState = RentState()
                    )
                }
            }
        }
    }

    private fun getStartTime() = profile?.activeRent?.startedAt

    private fun startTimer() {
        Log.d("CUSTOMER_RENT_POINT_VM", "startTimer")
        stopTimer()
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d("CUSTOMER_RENT_POINT_VM", "startTimer, insideLaunch")
            while (true) {
                getStartTime()?.let {
                    val elapsedTime = System.currentTimeMillis() - it
                    updateTime(elapsedTime)
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
        Log.d("CUSTOMER_RENT_POINT_VM", "updateTime: timeInMillis=$timeInMillis")
        profile?.activeRent?.let { rent ->
            _customerRentPointState.update {
                it.copy(
                    rentState = it.rentState.copy(
                        rentTime = Utils.millisToHumanTime(timeInMillis),
                        cost = Utils.calculateCost(
                            timeInMillis = timeInMillis,
                            rate = rent.rate,
                            productsCount = rent.products.size),
                        items = rent.products,
                    )
                )
            }
        }
    }

    override fun onCleared() {
        stopTimer()
        super.onCleared()
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