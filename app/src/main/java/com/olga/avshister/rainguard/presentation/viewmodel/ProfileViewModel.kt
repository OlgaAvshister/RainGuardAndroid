package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthLocalRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.profile.Role
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository: AuthRepository = AuthLocalRepository(application)

    private val _state =  MutableStateFlow<ProfileState>(ProfileState(isLoading = true))
    val state: StateFlow<ProfileState> = _state

    init {
        setProfile()
    }

    data class ProfileState(
        val isLoading: Boolean = true,
        val userIsLogged: Boolean = true,
        val name: String? = null,
        val phone: String? = null,
        val error: String? = null,
    )

    private fun setProfile() {
        viewModelScope.launch {
            with(authRepository.getProfile()) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    name = this?.name,
                    phone = this?.phone,
                )
            }
        }
    }


    fun onIntent(intent: Intent) {
        when (intent) {
            Intent.Logout -> {
                viewModelScope.launch {
                    try {
                        _state.value = _state.value.copy(isLoading = true)
                        authRepository.logout()
                        _state.value = _state.value.copy(
                            isLoading = false,
                            userIsLogged = false
                        )
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Не удалось выйти из профиля"
                        )
                    }
                }
            }
            else -> {}
        }
    }

    sealed interface Intent {
        object Logout: Intent
    }

}