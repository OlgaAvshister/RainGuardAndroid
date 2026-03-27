package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.profile.Role
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.state.MapMainContentState
import com.olga.avshister.rainguard.presentation.state.rent.RentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application): AndroidViewModel(application) {
    /**
     * Пока используем локальную реализацию репозитория, после разработки сервера нужно будет
     * переключить на RentPointRemoteRepository
     */
    val rentPointRepository: RentPointRepository = RentPointLocalRepository
    val authRepository: AuthRepository = AuthLocalRepository(application)

    private val _state = MutableStateFlow(
        MapMainContentState(
            isLoading = true,
            rentPoints = emptyList(),
            role = Role.CUSTOMER,
            hasActiveRent = false
        )
    )

    val mainContentState: StateFlow<MapMainContentState> = _state.asStateFlow()

    fun onIntent(bSheetState: BSheetContentState) {
        when (bSheetState) {
            BSheetContentState.IdleState -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val rentPoints = getRentPoints().toList() // создаем новый объект для обновления состояния
                    Log.d("onIntent", "MapViewModel, rentPoints size=${rentPoints.size},  rentPoints: $rentPoints")
                    authRepository.getProfile()?.let {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            role = it.role,
                            hasActiveRent = it.activeRent != null,
                            rentPoints = rentPoints
                        )
                    }
                }
            }
            else -> {

            }
        }
    }

    private fun getRentPoints(): List<RentPoint> {
        return rentPointRepository.getRentPoints()
    }
}