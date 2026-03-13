package com.olga.avshister.rainguard.data.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


class PrefsRepositoryImpl(context: Context): PrefsRepository {
    private val gson: Gson = Gson()

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun setString(key: String, value: String) {
        sharedPreferences.edit()
            .putString(key, value)
            .apply()
    }

    override fun getString(key: String, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun setLong(key: String, value: Long) {
        sharedPreferences.edit()
            .putLong(key, value)
            .apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    override fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_PROFILE = "key_profile"
        private const val KEY_CART = "key_cart"
    }
}