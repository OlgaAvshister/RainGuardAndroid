package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.profile.Role

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository: AuthRepository = AuthLocalRepository(application)
    fun getRole(): Role {
        return authRepository.getProfile()?.role ?: Role.CUSTOMER
    }

    fun isLogged(): Boolean {
        return (authRepository.getProfile() != null)
    }
}