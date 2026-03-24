package ua.nure.nomnomsave.ui.auth.forgotpassword

import ua.nure.nomnomsave.BuildConfig
import ua.nure.nomnomsave.navigation.Screen

object ForgotPassword {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data class OnEmailChange(val email: String) : Action
        data object OnResetPassword : Action
        data object OnResendCode : Action
        data object OnDismissCodeDialog : Action
        data object OnDismissPasswordDialog : Action
        data class OnVerifyCode(val code: String) : Action
        data class OnNewPassword(val password: String) : Action
    }

    data class State(
        val email: String = if (BuildConfig.DEBUG) "john.dow@gmail.com" else "",
        val showVerificationDialog: Boolean = false,
        val showNewPasswordDialog: Boolean = false,
        val emailError: String? = null,
        val codeError: String? = null,
        val inProgress: Boolean = false,
    )
}