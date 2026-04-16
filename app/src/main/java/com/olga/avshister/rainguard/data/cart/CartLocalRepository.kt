package com.olga.avshister.rainguard.data.cart

import com.olga.avshister.rainguard.domain.cart.Cart

object CartLocalRepository: CartRepository {
    private var cart = Cart(arrayListOf())

    override suspend fun getCart(): Cart? {
        return cart
    }

    override suspend fun addToCart(article: Long) {
        cart.products.add(article)
    }

    override suspend fun removeFromCart(article: Long) {
        cart.products.remove(article)
    }

    override suspend fun clearCart() {
        cart.products.clear()
    }

    override fun getCurrentRentPointId(): Long {
        TODO("Not yet implemented")
    }

    override fun setCurrentRentPointId(id: Long) {
        TODO("Not yet implemented")
    }
}