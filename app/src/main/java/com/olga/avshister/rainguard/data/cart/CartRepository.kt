package com.olga.avshister.rainguard.data.cart

import com.olga.avshister.rainguard.domain.cart.Cart

interface CartRepository {
    suspend fun getCart(): Cart?
    suspend fun addToCart(article: Long)
    suspend fun removeFromCart(article: Long)
    suspend fun clearCart()
    fun getCurrentRentPointId(): Long
    fun setCurrentRentPointId(id: Long)
}