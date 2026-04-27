package com.olga.avshister.rainguard.presentation.viewmodel.owner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthRemoteRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.products.Product
import com.olga.avshister.rainguard.domain.rent.Rent
import com.olga.avshister.rainguard.domain.rent.RentPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnerRentPointViewModel(application: Application): AndroidViewModel(application) {

    val authRepository: AuthRepository = AuthRemoteRepository(application)
    val rentPointRepository: RentPointRepository = RentPointRemoteRepository(application)

    private val _uiState = MutableStateFlow(OwnerUiState())
    val uiState: StateFlow<OwnerUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<OwnerEvent>()
    val event: SharedFlow<OwnerEvent> = _event.asSharedFlow()

    private val _financialData = MutableStateFlow<List<Rent>>(emptyList())
    val financialData: StateFlow<List<Rent>> = _financialData.asStateFlow()

    // Добавление сотрудника
    fun registerStuff(name: String, phone: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                authRepository.registerStuff(name, phone)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showAddEmployeeDialog = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка при добавлении сотрудника: ${e.message}"
                    )
                }
            }
        }
    }

    // Добавление товара
    fun addProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Получаем текущий RentPoint
                val currentRentPoint = _uiState.value.rentPoint
                if (currentRentPoint != null) {
                    rentPointRepository.addProduct( currentRentPoint.id, product)
                    val updatedProducts = currentRentPoint.availableProducts + product
                    val updatedRentPoint = currentRentPoint.copy(
                        availableProducts = updatedProducts
                    )
                    _uiState.update {
                        it.copy(
                            rentPoint = updatedRentPoint,
                            isLoading = false,
                            showAddProductDialog = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка при добавлении товара: ${e.message}"
                    )
                }
            }
        }
    }

    // Получение финансовых данных
    fun loadFinancialData(rentPointId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val rents = rentPointRepository.getCompletedRents(rentPointId)
                _financialData.value = rents
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showFinancialDialog = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка при загрузке финансов: ${e.message}"
                    )
                }
            }
        }
    }

    // Получение информации о пункте выдачи
    fun loadRentPoint(rentPointId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val rentPoint = rentPointRepository.getRentPointById(rentPointId)
                _uiState.update {
                    it.copy(
                        rentPoint = rentPoint,
                        isLoading = false,
                        error = null
                    )
                }
                loadProductsStatus(rentPointId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка при загрузке пункта выдачи: ${e.message}"
                    )
                }
            }
        }
    }

    // Получение статуса товаров
    private fun loadProductsStatus(rentPointId: Long) {
        viewModelScope.launch {
            try {
                val rentPointProducts = rentPointRepository.searchProducts(filter = null, rentPointId = rentPointId)

                val status = ProductsStatus(
                    ready = rentPointProducts.filter { it.condition == Product.ProductCondition.READY }.size,
                    dirty = rentPointProducts.filter { it.condition == Product.ProductCondition.DIRTY }.size,
                    broken = rentPointProducts.filter { it.condition == Product.ProductCondition.BROKEN }.size
                )
                _uiState.update { it.copy(productsStatus = status) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка получения статуса товаров: ${e.message}"
                    )
                }
            }
        }
    }

    // Удаление пункта выдачи
    fun deleteRentPoint(rentPointId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                rentPointRepository.deleteRentPoint(rentPointId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showDeleteConfirmation = false
                    )
                }
                _event.emit(OwnerEvent.RentPointDeleted)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showDeleteConfirmation = false,
                        error = "Ошибка при удалении пункта выдачи: ${e.message}"
                    )
                }
            }
        }
    }
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun showFinancialDialog(show: Boolean) {
        _uiState.update { it.copy(showFinancialDialog = show) }
    }

    fun showDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirmation = show) }
    }
}

// OwnerUiState.kt
data class OwnerUiState(
    val rentPoint: RentPoint? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddEmployeeDialog: Boolean = false,
    val showAddProductDialog: Boolean = false,
    val showFinancialDialog: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val productsStatus: ProductsStatus = ProductsStatus()
)

sealed class OwnerEvent {
    object RentPointDeleted : OwnerEvent()
}

data class ProductsStatus(
    val ready: Int = 0,
    val dirty: Int = 0,
    val broken: Int = 0
)