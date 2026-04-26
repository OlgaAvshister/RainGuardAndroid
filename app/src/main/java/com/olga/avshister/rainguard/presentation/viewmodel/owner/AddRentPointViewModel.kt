package com.olga.avshister.rainguard.presentation.viewmodel.owner

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.rent.RentPoint
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class AddRentPointViewModel(application: Application): AndroidViewModel(application) {
    private val rentPointRepository: RentPointRepository = RentPointRemoteRepository(application)
    private val _inputState = MutableStateFlow(InputRentPointParamState())

    val inputState: StateFlow<InputRentPointParamState> = _inputState.asStateFlow()

    private val _events = MutableSharedFlow<Event?>(0)
    val events: SharedFlow<Event?> = _events.asSharedFlow()

    fun onIntent(intent: AddRentPointIntent) {
        when (intent) {
            is AddRentPointIntent.SaveRentPoint -> {
                viewModelScope.launch {
                    _inputState.value = _inputState.value.copy(isLoading = true)
                    getCoordinatesByAddress(_inputState.value.address,
                        onResult = { point, fullAddress ->
                            viewModelScope.launch {
                                _inputState.value = _inputState.value.copy(isLoading = false)
                                rentPointRepository.registerRentPoint(
                                    RentPoint(
                                        id = Random.nextLong(),
                                        address = fullAddress,
                                        name = _inputState.value.name,
                                        latitude = point.latitude,
                                        longitude = point.longitude,
                                        workHours = _inputState.value.workHours,
                                        availableProducts = listOf()
                                    )
                                )
                                _events.emit(Event.Close)
                            }
                        },
                        onError = { error ->
                            viewModelScope.launch {
                                _inputState.value = _inputState.value.copy(isLoading = false)
                                _events.emit(Event.Error(error))
                            }
                        }
                    )
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

    fun getCoordinatesByAddress(address: String, onResult: (Point, String) -> Unit, onError: (error: String) -> Unit) {
        val searchManager: SearchManager = SearchFactory.getInstance()
        .createSearchManager(SearchManagerType.COMBINED)

        val options = SearchOptions().apply {
            searchTypes = SearchType.NONE.value // поиск не только по адресу? но и по названию объекта
            resultPageSize = 1 // нужен только первый результат
        }

        // Создаем BoundingBox для всей территории России
        // Юго-западный угол: 41°N, 19°E (Калининградская область)
        // Северо-восточный угол: Чукотка
        val southWest = Point(41.0, 19.0)
        val northEast = Point(82.0, 190.0)
        val boundingBox = BoundingBox(southWest, northEast)

        // Для оптимизации своих запросов Яндекс требует указывать границы поиска на карте
        val geometry = Geometry.fromBoundingBox(boundingBox)

        searchManager.submit(
            address,
            geometry,
            options,
            object: Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    try {
                        Log.d("SearchManager", "response: ${response.collection}")
                        val children = response.collection.children
                        val geoObject = children[0].obj
                        val point = (geoObject?.geometry[0])?.point!!

                        val toponymMetadata = geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java)

                        val components = toponymMetadata?.address?.components
                        var fullAddress = geoObject.name

                        if (components != null && components.isNotEmpty()) {
                            // Собираем адрес из компонентов, исключая дублирование названия
                            val addressParts = components
                                .mapNotNull { it.name }
                                .filter { it != geoObject.name } // Убираем дубликат
                                .take(3) // Берем несколько компонентов для читаемости

                            if (addressParts.isNotEmpty()) {
                                fullAddress = "$fullAddress, ${addressParts.joinToString(", ")}"
                            }
                        } else if (geoObject.descriptionText?.isNotEmpty() == true) {
                            fullAddress = "$fullAddress, ${geoObject.descriptionText}"
                        }

                        Log.d("SearchManager", "point=$point, fullAddress: $fullAddress")
                        onResult(point, fullAddress.orEmpty())
                    } catch (e: Exception) {
                        onError("Ошибка получения координат по введенному адресу")
                    }
                }

                override fun onSearchError(p0: Error) {
                    Log.d("SearchManager", "onSearchError: $p0")
                    onError("Ошибка получения координат по введенному адресу")
                }
            }
        )
    }
    sealed class AddRentPointIntent {
        object SaveRentPoint: AddRentPointIntent()
        class OnRentPointNameChanged(val name: String): AddRentPointIntent()
        class OnRentPointAddressChanged(val address: String): AddRentPointIntent()
        class OnRentPointWorkHoursChanged(val workHours: String): AddRentPointIntent()
    }

    data class InputRentPointParamState(
        val isLoading: Boolean = false,
        val name: String = "",
        val address: String = "",
        val workHours: String = "",
    )

    sealed class Event {
        object Close : Event()
        data class Error(val message: String) : Event()
    }
}