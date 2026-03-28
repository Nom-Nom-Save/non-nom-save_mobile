package ua.nure.nomnomsave.ui.favorite

import android.graphics.Bitmap
import ua.nure.nomnomsave.db.data.entity.Favorite
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.navigation.Screen

object Favorite {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data class OnQueryChanged(val query: String) : Action
        data class OnDeleteFavorite(val favoriteId: String, val establishmentId: String) : Action
    }

    data class State(
        val inProgress: Boolean = false,
        val query: String? = null,
        val favorites: List<Favorite>? = null
    )
}