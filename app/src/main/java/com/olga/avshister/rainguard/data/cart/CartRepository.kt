package com.olga.avshister.rainguard.data.cart

import com.olga.avshister.rainguard.domain.cart.Cart

interface CartRepository {
    fun getCart(): Cart?
    fun addToCart(article: Long)
    fun removeFromCart(article: Long)
    fun clearCart()
    fun getCurrentRentPointId(): Long
    fun setCurrentRentPointId(id: Long)
}