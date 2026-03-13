package com.olga.avshister.rainguard.data.common


interface PrefsRepository {

    fun setString(key: String, value: String)
    fun getString(key: String, defaultValue: String? = null): String?

    fun setLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0): Long

    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
}

