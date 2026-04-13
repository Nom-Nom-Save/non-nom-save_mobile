package ua.nure.nomnomsave.ui.maps

import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.ui.profile.Profile

object Maps {

    sealed interface Event{
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data class OnEstablishmentSelected(val establishment: EstablishmentEntity?) : Action
    }

    data class State(
        val establishments: List<EstablishmentEntity>? = emptyList(),
        val selectedEstablishment: EstablishmentEntity? = null,
    )
}