package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.rent.RentRepository
import com.olga.avshister.rainguard.data.rent.RentRepositoryImpl
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.presentation.state.rent.RentState
import com.olga.avshister.rainguard.presentation.ui.utils.Utils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CurrentRentViewModel(application: Application) : AndroidViewModel(application) {
    private val rentRepository: RentRepository = RentRepositoryImpl(application)
    private val rentPointRepository: RentPointRepository = RentPointRemoteRepository(application)

    private val _state = MutableStateFlow(RentState(isLoading = true))
    val state: StateFlow<RentState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var startTime = System.currentTimeMillis()

    sealed class Intent {
        object LoadRentData : Intent()
        object StartTimer : Intent()
        object StopTimer : Intent()
        object OnCloseButton : Intent()
        data class UpdateTime(val timeInMillis: Long) : Intent()
    }

    sealed class Event {
        object Close : Event()
        data class Error(val message: String) : Event()
    }

    private val _events = MutableSharedFlow<Event?>(0)
    val events: SharedFlow<Event?> = _events.asSharedFlow()

    init {
        handleIntent(Intent.LoadRentData)
        handleIntent(Intent.StartTimer)
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadRentData -> loadRentData()
            is Intent.StartTimer -> startTimer()
            is Intent.StopTimer -> stopTimer()
            is Intent.OnCloseButton -> closeBSheet()
            is Intent.UpdateTime -> updateTime(intent.timeInMillis)
        }
    }

    private fun loadRentData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val activeRent = rentRepository.getActiveRent()
                activeRent?.let { rent ->
                    startTime = activeRent.startedAt
                    val rentedProducts = rentPointRepository.searchProducts(
                        ids = activeRent.productIds,
                        rentPointId = activeRent.startRentPointId!!
                    )
                    _state.value = _state.value.copy(
                        items = rentedProducts,
                        rentTime = "00:00:00",
                        rate = rent.rate,
                        isLoading = false,
                        cost = 0,
                    )
                } ?: _events.emit(Event.Error(message = "Активная аренда не найдена"))

            } catch (e: Exception) {
                _events.emit(Event.Error(message = e.message.toString()))
            }
        }
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsedTime = System.currentTimeMillis() - startTime
                handleIntent(Intent.UpdateTime(elapsedTime))
                delay(1000) // Обновляем каждую секунду
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun updateTime(timeInMillis: Long) {
        _state.value = _state.value.copy(
            rentTime = Utils.millisToHumanTime(timeInMillis),
            cost = Utils.calculateCost(
                timeInMillis = timeInMillis,
                rate = _state.value.rate,
                productsCount = _state.value.items.size)
        )
    }

    private fun closeBSheet() {
        viewModelScope.launch {
            stopTimer()
            // Здесь будет API вызов для завершения аренды
            _events.emit(Event.Close)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}