package ua.nure.nomnomsave.ui.establishmentDetails

import android.view.Menu
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.navigation.Screen

object EstablishmentDetails {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
    }

    data class State(
        val establishment: EstablishmentEntity? = null,
        val inProgress: Boolean = false,
        val menu: List<MenuEntity>? = null,
        )
}