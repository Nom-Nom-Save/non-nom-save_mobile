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

    @Serializable
    data object Profile : Screen()

    @Serializable
    sealed class Cart : Screen() {
        @Serializable
        data object CartList : Cart()
    }

    @Serializable
    sealed class List: Screen() {
        @Serializable
        data object ListView : List()
        @Serializable
        data object Establishment : List()
    }
}