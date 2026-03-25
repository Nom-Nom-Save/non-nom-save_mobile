package ua.nure.nomnomsave.ui.auth.register

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
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.auth.AuthRepository
import ua.nure.nomnomsave.repository.onError
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.resource.ResourceRepository
import ua.nure.nomnomsave.ui.auth.register.Register.Event.*
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val resourceRepository: ResourceRepository,
) : ViewModel() {

    private val TAG by lazy { RegisterViewModel::class.simpleName }
    private val _state = MutableStateFlow(Register.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Register.Event>()
    val event = _event.asSharedFlow()

    private var registrationJob: Job? = null
    private var verifyCodeJob: Job? = null

    fun onAction(action: Register.Action) = viewModelScope.launch {
        when (action) {
            Register.Action.OnBack -> {
                _event.emit(OnBack)
            }

            is Register.Action.OnNavigate -> {
                _event.emit(OnNavigate(route = action.route))
            }

            is Register.Action.OnEmailChange -> {
                _state.update { s -> s.copy(email = action.email, emailError = null) }
            }

            is Register.Action.OnNameChange -> {
                _state.update { s -> s.copy(name = action.name, nameError = null) }
            }

            is Register.Action.OnPasswordChange -> {
                _state.update { s -> s.copy(password = action.password, passwordError = null) }
            }

            is Register.Action.OnPrivacyPolicyAgreementChange -> {
                _state.update { s -> s.copy(isPrivacyPolicyAgreed = action.isAgreed) }
            }

            Register.Action.OnRegister -> {
                register(
                    name = state.value.name,
                    email = state.value.email,
                    password = state.value.password
                )
            }

            is Register.Action.OnVerificationEmailCode -> {
                verifyEmail(code = action.code, email = state.value.email)
            }

            Register.Action.OnGoogle -> {}
            is Register.Action.OnShowVerificationDialog -> {
                _state.update { s -> s.copy(showVerificationDialog = action.state) }
            }
        }
    }

    private fun verifyEmail(code: String, email: String) {
        verifyCodeJob?.cancel()
        verifyCodeJob = viewModelScope.launch {
            authRepository.verifyEmail(
                email = email,
                code = code
            ).onSuccess {
                _event.emit(Register.Event.OnNavigate(route = Screen.Auth.SignIn))
                _state.update { s ->
                    s.copy(
                        showVerificationDialog = false
                    )
                }

            }.onError {

            }
        }
    }

    private fun register(
        name: String,
        email: String,
        password: String
    ) {
        if (name.isEmpty()) {
            _state.update { s ->
                s.copy(
                    nameError = resourceRepository.getStringByResource(R.string.nameIsEmpty)
                )
            }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update { s ->
                s.copy(
                    emailError = resourceRepository.getStringByResource(R.string.emailNotValid)
                )
            }
            return
        }
        if (password.isEmpty() || password.length <= 5) {
            _state.update { s ->
                s.copy(
                    passwordError = resourceRepository.getStringByResource(R.string.passwordNotValid)
                )
            }
            return
        }

        registrationJob?.cancel()
        registrationJob = viewModelScope.launch {
            authRepository.register(
                name = name,
                email = email,
                password = password
            ).onSuccess { message ->
                _state.update { s ->
                    s.copy(
                        showVerificationDialog = true
                    )
                }
            }.onError { error: DataError ->
                Log.e(TAG, "Registration error: $error")
            }
        }

    }
}