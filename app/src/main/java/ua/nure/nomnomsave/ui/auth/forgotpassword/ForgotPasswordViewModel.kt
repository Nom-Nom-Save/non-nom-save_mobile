package ua.nure.nomnomsave.ui.auth.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.repository.auth.AuthRepository
import ua.nure.nomnomsave.repository.onError
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.resource.ResourceRepository
import ua.nure.nomnomsave.ui.auth.forgotpassword.ForgotPassword.Event.*
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val resourceRepository: ResourceRepository,
) : ViewModel() {
    private val TAG by lazy { ForgotPasswordViewModel::class.simpleName }

    private val _state = MutableStateFlow(ForgotPassword.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ForgotPassword.Event>()
    val event = _event.asSharedFlow()

    private var resetPasswordJob: Job? = null
    private var verifyCodeJob: Job? = null
    private var newPasswordJob: Job? = null

    fun onAction(action: ForgotPassword.Action) = viewModelScope.launch {
        when (action) {
            ForgotPassword.Action.OnBack -> _event.emit(OnBack)

            is ForgotPassword.Action.OnNavigate -> _event.emit(OnNavigate(route = action.route))

            is ForgotPassword.Action.OnEmailChange -> _state.update { s ->
                s.copy(email = action.email, emailError = null)
            }

            ForgotPassword.Action.OnResetPassword -> resetPassword(email = state.value.email)

            ForgotPassword.Action.OnResendCode -> resendCode()

            is ForgotPassword.Action.OnVerifyCode -> verifyCode(
                code = action.code,
                email = state.value.email
            )

            ForgotPassword.Action.OnDismissCodeDialog -> _state.update { s ->
                s.copy(showVerificationDialog = false, codeError = null)
            }

            ForgotPassword.Action.OnDismissPasswordDialog -> _state.update { s ->
                s.copy(showNewPasswordDialog = false)
            }

            is ForgotPassword.Action.OnNewPassword -> newPassword(
                email = state.value.email,
                password = action.password
            )
        }
    }

    private fun resetPassword(email: String) {
        resetPasswordJob?.cancel()
        resetPasswordJob = viewModelScope.launch {
            _state.update { it.copy(inProgress = true) }
            authRepository.forgotPassword(email = email)
                .onSuccess {
                    _state.update { s ->
                        s.copy(showVerificationDialog = true)
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            emailError = resourceRepository.getStringByResource(R.string.emailNotValid)
                        )
                    }
                    Log.e(TAG, "resetPassword error: $error")
                }
            _state.update { it.copy(inProgress = false) }
        }
    }

    private fun resendCode() {
        viewModelScope.launch {
            authRepository.forgotPassword(email = state.value.email)
                .onSuccess { Log.d(TAG, "Code resent successfully") }
                .onError { Log.e(TAG, "Resend error: $it") }
        }
    }

    private fun verifyCode(code: String, email: String) {
        verifyCodeJob?.cancel()
        verifyCodeJob = viewModelScope.launch {
            authRepository.verifyResetCode(email = email, code = code)
                .onSuccess {
                    _state.update { s ->
                        s.copy(
                            showVerificationDialog = false,
                            showNewPasswordDialog = true,
                            codeError = null
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            codeError = resourceRepository.getStringByResource(R.string.code)
                        )
                    }
                    Log.e(TAG, "verifyCode error: $error")
                }
        }
    }

    private fun newPassword(password: String, email: String) {
        newPasswordJob?.cancel()
        newPasswordJob = viewModelScope.launch {
            authRepository.resetPassword(email = email, newPassword = password)
                .onSuccess {
                    _state.update { s ->
                        s.copy(showNewPasswordDialog = false)
                    }
                    _event.emit(OnNavigate(route = Screen.Auth.SignIn))
                }
                .onError { error ->
                    Log.e(TAG, "newPassword error: $error")
                }
        }
    }
}