package ua.nure.nomnomsave

import ua.nure.nomnomsave.navigation.Screen

sealed interface PushEvent {
    data class EstablishmentDetails(val id: String): PushEvent
}

fun PushEvent.toScreen() =
    when(this) {
        is PushEvent.EstablishmentDetails -> Screen.List.EstablishmentDetails(id = this.id)
    }