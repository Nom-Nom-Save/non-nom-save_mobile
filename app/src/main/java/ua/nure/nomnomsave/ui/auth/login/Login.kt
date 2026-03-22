package ua.nure.nomnomsave.ui.auth.login

import ua.nure.nomnomsave.BuildConfig
import ua.nure.nomnomsave.navigation.Screen

object Login {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data class OnGoogleLogIn(val idToken: String, val email: String) : Action
        data object OnLogIn : Action
        data class OnEmailChange(val email: String) : Action
        data class OnPasswordChange(val password: String) : Action
    }

    data class State(
        val inProgress: Boolean = false,
        val email: String = if (BuildConfig.DEBUG) "john.dow@gmail.com" else "",
        val password: String = if (BuildConfig.DEBUG) "Secret1" else "",
        val loginError: String? = null,
    )
}