package com.olga.avshister.rainguard.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.olga.avshister.rainguard.data.profile.AuthRemoteRepository
import com.olga.avshister.rainguard.data.profile.AuthRepository
import com.olga.avshister.rainguard.domain.profile.Profile
import com.olga.avshister.rainguard.domain.profile.Role
import com.olga.avshister.rainguard.presentation.core.MAP_SCREEN
import com.olga.avshister.rainguard.presentation.core.SELECT_PRODUCT_TO_CHECK_SCREEN
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SmsCodeViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository: AuthRepository = AuthRemoteRepository(application)

    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action.asSharedFlow()
    private suspend fun auth(phone: String, code: String): Profile {
        val profile = authRepository.auth(phone, code)
        return profile
    }

    sealed interface Intent {
        data class Auth(val phone: String, val code: String): Intent
    }

    sealed class Action {
        data class NavigateToScreen(val screen: String) : Action()
        data class ShowError(val errorMessage: String) : Action()
    }

    fun onIntent(intent: Intent) {
        when (intent) {
            is Intent.Auth -> {
                viewModelScope.launch {
                    try {
                        val profile = auth(intent.phone, intent.code)
                        Log.d(SMS_CODE_VM_TAG, "authorized as user=$profile")
                        when (profile.role) {
                            Role.CUSTOMER -> {
                                _action.emit(Action.NavigateToScreen(MAP_SCREEN))
                            }
                            Role.STUFF -> {
                                _action.emit(Action.NavigateToScreen(SELECT_PRODUCT_TO_CHECK_SCREEN))
                            }
                            Role.OWNER -> {
                                _action.emit(Action.NavigateToScreen(MAP_SCREEN))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(SMS_CODE_VM_TAG, "auth call Ошибка: message=${e.message}, cause=${e.cause}")
                        _action.emit(Action.ShowError("Неверный код подтверждения. Пожалуйста, проверьте SMS и попробуйте снова"))
                    }
                }
            }
        }
    }

    companion object {
        const val SMS_CODE_VM_TAG = "SMS_CODE_VM"
    }
}