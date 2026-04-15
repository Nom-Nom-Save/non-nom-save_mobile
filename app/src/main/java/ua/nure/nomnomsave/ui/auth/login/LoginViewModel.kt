package ua.nure.nomnomsave.ui.auth.login

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
import ua.nure.nomnomsave.ui.auth.login.Login
import ua.nure.nomnomsave.ui.auth.login.Login.Event.*
import javax.inject.Inject
import kotlin.onSuccess

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val resourceRepository: ResourceRepository,
) : ViewModel() {
    private val TAG by lazy { LoginViewModel::class.simpleName }
    private val _state = MutableStateFlow(Login.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Login.Event>()
    val event = _event.asSharedFlow()

    private var loginJob: Job? = null
    private var googleLogInJob: Job? = null

    fun onAction(action: Login.Action) = viewModelScope.launch {
        when(action) {
            Login.Action.OnBack -> {
                _event.emit(Login.Event.OnBack)
            }
            is Login.Action.OnNavigate -> {
                _event.emit(OnNavigate(route = action.route))
            }
            is Login.Action.OnEmailChange -> {
                _state.update { s -> s.copy(email = action.email, loginError = null) }
            }
            is Login.Action.OnPasswordChange -> {
                _state.update { s -> s.copy(password = action.password, loginError = null) }
            }

            Login.Action.OnLogIn -> {
                login(email = state.value.email, password = state.value.password )
            }
            is Login.Action.OnGoogleLogIn -> onGoogleLogin(idToken = action.idToken, email = action.email)
        }
    }


    private fun login(
        email: String,
        password: String
    ) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            authRepository.login(
                email = email,
                password = password
            ).onSuccess {
                _event.emit(
                    Login.Event.OnNavigate(route = Screen.List.ListView)
                )
            }.onError { error ->
                _state.update { it.copy(loginError = resourceRepository.getStringByResource(R.string.noMatches)) }
                Log.e(TAG, "Login error: $error")
            }
        }
    }

    private fun onGoogleLogin(idToken: String, email: String) {
        googleLogInJob?.cancel()
        googleLogInJob = viewModelScope.launch {
            authRepository.googleLogIn(
                token = idToken,
                email = email
            ).onSuccess {
                _event.emit(
                    Login.Event.OnNavigate(route = Screen.List.ListView)
                )
            }.onError { error ->
                _state.update { it.copy(loginError = resourceRepository.getStringByResource(R.string.noMatches)) }
                Log.e(TAG, "Google Login error: $error")
            }
        }
    }
}