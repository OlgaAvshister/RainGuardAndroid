package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.presentation.state.rent.RentTotalState
import com.olga.avshister.rainguard.presentation.ui.utils.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RentTotalViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository: AuthRepository = AuthLocalRepository(application)
    private val _state = MutableStateFlow(
        RentTotalState(
            totalTime = "",
            totalCost = "",
            isLoading = false
        )
    )

    val state: StateFlow<RentTotalState> = _state

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val activeRent = authRepository.getProfile()?.activeRent
            activeRent?.let {
                val elapsedTime = System.currentTimeMillis() - activeRent.startedAt
                val cost = Utils.calculateCost(
                    timeInMillis = elapsedTime,
                    rate = activeRent.rate,
                    productsCount = activeRent.products.size
                )

                val time = Utils.millisToHumanTime(elapsedTime)

                _state.update {
                    it.copy(
                        totalTime = time,
                        totalCost = "${cost}₽",
                        isLoading = false
                    )
                }
            }
        }
    }
}