package ua.nure.nomnomsave.ui.profile

import ua.nure.nomnomsave.db.data.entity.ProfileEntity
import ua.nure.nomnomsave.navigation.Screen

object Profile {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action

        data class OnNameChange(val name: String?) : Action
        data class OnEmailChange(val email: String?) : Action
        data class OnAvatarChange(val avatarUrl: String?) : Action

        data object OnDismissChangeAvatarDialog : Action
        data object OnShowChangeAvatarDialog : Action

        data object OnNearbyDealsChange : Action
        data object OnCloseTimeChange : Action

        data object OnSave: Action
    }

    data class State(
        val profile: ProfileEntity? = null,
        val inProgress: Boolean = false,
        val showChangeAvatarDialog: Boolean = false,
        val closedTimeNotifications: Boolean = false,
        val nearbyDealsNotifications: Boolean = false,
        val nameError: String? = null,
        val emailError: String? = null,
    )
}