package com.olga.avshister.rainguard.presentation.viewmodel.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.rent_point.RentPointLocalRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.rent.RentPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class AddRentPointViewModel(application: Application): AndroidViewModel(application) {
    val rentPointRepository: RentPointRepository = RentPointLocalRepository
    private val _inputState = MutableStateFlow(InputRentPointParamState())

    val inputState: StateFlow<InputRentPointParamState> = _inputState.asStateFlow()

    fun onIntent(intent: AddRentPointIntent) {
        when (intent) {
            is AddRentPointIntent.SaveRentPoint -> {
                val coordinates: Pair<Double, Double> = getCoordinatesByAddress(_inputState.value.address)
                viewModelScope.launch {
                    _inputState.value = _inputState.value.copy(isLoading = true)
                    rentPointRepository.registerRentPoint(
                        RentPoint(
                            id = Random.nextLong(),
                            address = _inputState.value.address,
                            name = _inputState.value.name,
                            latitude = coordinates.first,
                            longitude = coordinates.second,
                            workHours = _inputState.value.workHours,
                            availableProducts = listOf()
                        )
                    )
                    _inputState.value = _inputState.value.copy(isLoading = false)
                }
            }
            is AddRentPointIntent.OnRentPointNameChanged -> {
                _inputState.value = _inputState.value.copy(name = intent.name)
            }
            is AddRentPointIntent.OnRentPointAddressChanged -> {
                _inputState.value = _inputState.value.copy(address = intent.address)
            }
            is AddRentPointIntent.OnRentPointWorkHoursChanged -> {
                _inputState.value = _inputState.value.copy(workHours = intent.workHours)
            }
        }
    }

    fun getCoordinatesByAddress(address: String): Pair<Double, Double> {
        // здесь нужно использовать геокодер для получения из адреса координат
        return Pair(55.752515, 37.621578)
    }

    sealed class AddRentPointIntent {
        object SaveRentPoint: AddRentPointIntent()
        class OnRentPointNameChanged(val name: String): AddRentPointIntent()
        class OnRentPointAddressChanged(val address: String): AddRentPointIntent()
        class OnRentPointWorkHoursChanged(val workHours: String): AddRentPointIntent()
    }

    data class InputRentPointParamState(
        val isLoading: Boolean = true,
        val name: String = "",
        val address: String = "",
        val workHours: String = "",
    )
}