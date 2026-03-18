package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PayInProgressViewModel(application: Application): AndroidViewModel(application) {
    val authRepository: AuthRepository = AuthLocalRepository(application)

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        State(isLoading = true, isPayFinished = false)
    )
    val state: MutableStateFlow<State> = _state

    val mockPayTime = 3000L

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
        authRepository.finishRent(timeNow = System.currentTimeMillis())
        authRepository.getProfile()?.let {
            authRepository.updateProfile(it.copy(activeRent = null))
        }
    }
}