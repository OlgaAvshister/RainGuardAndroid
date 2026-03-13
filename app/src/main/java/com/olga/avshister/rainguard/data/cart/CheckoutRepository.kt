package com.olga.avshister.rainguard.data.cart

import com.olga.avshister.rainguard.domain.Checkout

interface CheckoutRepository {
    fun getCheckout(): Checkout
    fun setCheckout(checkout: Checkout)
}