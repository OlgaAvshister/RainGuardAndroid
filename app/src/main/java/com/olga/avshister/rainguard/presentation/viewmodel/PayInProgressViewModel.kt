package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.rent.RentRepository
import com.olga.avshister.rainguard.data.rent.RentRepositoryImpl
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PayInProgressViewModel(application: Application): AndroidViewModel(application) {
    val rentPointRepository: RentPointRepository = RentPointRemoteRepository(application)
    val rentRepository: RentRepository = RentRepositoryImpl(application)

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        State(isLoading = true, isPayFinished = false)
    )
    val state: MutableStateFlow<State> = _state

    val mockPayTime = 2000L

    data class State(
        val isLoading: Boolean,
        val isPayFinished: Boolean,
    )

    init {
        processPay()
    }

    sealed interface Intent {
        object PaySuccess: Intent
    }

    private fun processPay() {
        viewModelScope.launch {
            delay(mockPayTime)
            withContext(Dispatchers.IO) { finishRent() }
            withContext(Dispatchers.Main) {
                onIntent(Intent.PaySuccess)
            }
        }
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            Intent.PaySuccess -> {
                state.value = state.value.copy(
                    isLoading = false,
                    isPayFinished = true)
            }
        }
    }

    private suspend fun finishRent() {
        val activeRent = rentRepository.getActiveRent()!!
        rentPointRepository.finishRent(
            activeRent.copy(
                finishedAt = System.currentTimeMillis(),
                finishRentPointId = rentPointRepository.getFinishRentPointId()
        ))
    }
}