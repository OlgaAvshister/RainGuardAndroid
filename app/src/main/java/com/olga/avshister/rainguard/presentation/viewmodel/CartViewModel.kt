package com.olga.avshister.rainguard.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.cart.CartLocalRepository
import com.olga.avshister.rainguard.data.cart.CartRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRemoteRepository
import com.olga.avshister.rainguard.data.rent_point.RentPointRepository
import com.olga.avshister.rainguard.domain.products.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(
    private val context: Context,
    private val rentPointId: Long,
    private val selectedArticles: Set<Long>
) : ViewModel() {
    private val rentPointRepository: RentPointRepository = RentPointRemoteRepository(context)
    private val cartLocalRepository: CartRepository = CartLocalRepository

    data class State(
        val cart: CartUI,
        val isLoading: Boolean,
        val error: String? = null
    )

    data class CartUI(
        val items: List<CartProductUI>,
    )

    data class CartProductUI(
        val product: Product,
        val currentQuantity: Int,
        val maxQuantity: Int,
    )

    private val _state = MutableStateFlow(
        State(
            cart = CartUI(emptyList()),
            isLoading = false,
            error = null
        )
    )

    val state: StateFlow<State> = _state

    // ---------- Intents ----------
    sealed interface Intent {
        data class AddToCart(val article: Long) : Intent
        data class RemoveFromCart(val article: Long) : Intent
    }

    init {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                // Очищаем корзину
                cartLocalRepository.clearCart()
                //authRepository.clearCart()

                // Создаем начальное состояние корзины
                val initialCart = getInitialCartState(rentPointId, selectedArticles)

                _state.update {
                    it.copy(
                        cart = initialCart,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Ошибка инициализации",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.AddToCart -> addToCart(intent.article)
            is Intent.RemoveFromCart -> removeFromCart(intent.article)
        }
    }

    private fun addToCart(article: Long) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                // Добавляем в репозиторий
                //authRepository.addToCart(article)
                cartLocalRepository.addToCart(article)

                // Обновляем состояние UI
                _state.update { currentState ->
                    val updatedItems = currentState.cart.items.map { item ->
                        if (item.product.article == article) {
                            // Увеличиваем количество, но не больше максимума
                            val newQuantity = item.currentQuantity + 1
                            if (newQuantity <= item.maxQuantity) {
                                item.copy(currentQuantity = newQuantity)
                            } else {
                                item // Не изменяем, если достигнут максимум
                            }
                        } else {
                            item
                        }
                    }

                    currentState.copy(
                        cart = CartUI(updatedItems),
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Ошибка добавления в корзину",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun removeFromCart(article: Long) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                val currentQuantity = _state.value.cart.items
                    .find { it.product.article == article }
                    ?.currentQuantity ?: 0

                if (currentQuantity > 0) {
                    //authRepository.removeFromCart(article)
                    cartLocalRepository.removeFromCart(article)
                }

                _state.update { currentState ->
                    val updatedItems = currentState.cart.items.map { item ->
                        if (item.product.article == article) {
                            // Уменьшаем количество, но не ниже 0
                            val newQuantity = (item.currentQuantity - 1).coerceAtLeast(0)
                            item.copy(currentQuantity = newQuantity)
                        } else {
                            item
                        }
                    }

                    currentState.copy(
                        cart = CartUI(updatedItems),
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Ошибка удаления из корзины",
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun getInitialCartState(rentPointId: Long, articles: Set<Long>): CartUI {
        return try {
            val items = articles.mapNotNull { article ->
                val productsByArticle = rentPointRepository.searchProducts(article, rentPointId)
                if (productsByArticle.isNotEmpty()) {
                    CartProductUI(
                        product = productsByArticle.first(),
                        currentQuantity = 0, // Начинаем с 0
                        maxQuantity = productsByArticle.size
                    )
                } else {
                    null
                }
            }
            CartUI(items)
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустую корзину
            CartUI(emptyList())
        }
    }

    class CartViewModelFactory(
        private val context: Context,
        private val rentPointId: Long,
        private val selectedArticles: Set<Long>,
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CartViewModel(context, rentPointId, selectedArticles) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
