package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.profile.Profile

class SmsCodeViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository: AuthRepository = AuthLocalRepository(application)
    fun auth(phone: String, code: String): Profile {
        val profile = authRepository.auth(phone, code)
        return profile
    }

}