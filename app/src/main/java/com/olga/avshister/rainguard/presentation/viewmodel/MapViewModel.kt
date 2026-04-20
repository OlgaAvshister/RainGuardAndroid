package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthRemoteRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.data.rent.RentRepository
import com.olga.avshister.rainguard.data.rent.RentRepositoryImpl
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.profile.Role
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.olga.avshister.rainguard.presentation.state.BSheetContentState
import com.olga.avshister.rainguard.presentation.state.MapMainContentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application): AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRemoteRepository(application)
    private val rentPointRepository: RentPointRepository = RentPointRemoteRepository(application)
    private val rentRepository: RentRepository = RentRepositoryImpl(application)

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
                    val activeRent = rentRepository.getActiveRent()
                    Log.d("onIntent", "MapViewModel, rentPoints size=${rentPoints.size},  rentPoints: $rentPoints")
                    authRepository.getProfile()?.let {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            role = it.role,
                            hasActiveRent = activeRent != null,
                            rentPoints = rentPoints
                        )
                    }
                }
            }
            else -> {

            }
        }
    }

    private suspend fun getRentPoints(): List<RentPoint> {
        return rentPointRepository.getRentPoints()
    }
}