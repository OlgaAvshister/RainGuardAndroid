package com.olga.avshister.rainguard.data.cart

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.olga.avshister.rainguard.data.common.PrefsRepository
import com.olga.avshister.rainguard.data.common.PrefsRepositoryImpl
import com.olga.avshister.rainguard.domain.Checkout

class CheckoutLocalRepository(context: Context): CheckoutRepository {
    private val prefs: PrefsRepository = PrefsRepositoryImpl(context)

    override fun getCheckout(): Checkout {
        val json = prefs.getString(KEY_CHECKOUT, "[]")
        val checkoutType = object : TypeToken<Checkout>() {}.type
        val checkout: Checkout = Gson().fromJson(json, checkoutType)
        return checkout
    }

    override fun setCheckout(checkout: Checkout) {
        val json = Gson().toJson(checkout)
        prefs.setString(KEY_CHECKOUT, json)
    }

    companion object {
        const val KEY_CHECKOUT = "KEY_CHECKOUT"
    }
}