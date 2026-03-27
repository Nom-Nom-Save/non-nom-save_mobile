package ua.nure.nomnomsave.ui.cart

import ua.nure.nomnomsave.BuildConfig
import ua.nure.nomnomsave.navigation.Screen

object Cart {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data class OnQueryChanged(val query: String) : Action
    }

    data class State(
        val inProgress: Boolean = false,
        val query: String? = null,
    )
}