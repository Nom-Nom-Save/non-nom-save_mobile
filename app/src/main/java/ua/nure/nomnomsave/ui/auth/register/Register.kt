package ua.nure.nomnomsave.ui.auth.register

import android.graphics.Bitmap
import ua.nure.nomnomsave.BuildConfig
import ua.nure.nomnomsave.navigation.Screen

public object Register {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data object OnRegister : Action
        data object OnGoogle: Action

        data class OnVerificationEmailCode(val code: String) : Action
        data class OnNameChange(val name: String) : Action
        data class OnEmailChange(val email: String) : Action
        data class OnPasswordChange(val password: String) : Action

        data class OnPrivacyPolicyAgreementChange(val isAgreed: Boolean) : Action
        data class OnShowVerificationDialog(val state: Boolean): Action
    }

    data class State(
        val inProgress: Boolean = false,
        val name: String = "",
        val email: String = if (BuildConfig.DEBUG) "john.dow@gmail.com" else "",
        val password: String = if (BuildConfig.DEBUG) "Secret1" else "",
        val isPrivacyPolicyAgreed: Boolean = false,
        val showVerificationDialog: Boolean = false,
        val code: String = "",
        val nameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
    )
}