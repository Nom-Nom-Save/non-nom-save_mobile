package ua.nure.nomnomsave.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object EmptyScreen : Screen()

    @Serializable
    sealed class Auth : Screen() {
        @Serializable
        data object Registration : Auth()
        @Serializable
        data object SignIn : Auth()
        @Serializable
        data object ForgotPassword : Auth()
    }
}