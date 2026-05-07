package ua.nure.nomnomsave.extention

import android.content.Intent
import ua.nure.nomnomsave.PushEvent

fun Intent.toPushEvent(): PushEvent? {
    val action = getStringExtra("ACTION") ?: return null

    return when(action) {
        "establishment_details" -> {
            PushEvent.EstablishmentDetails(id = getStringExtra("ID") ?: return null)
        }
        else -> null
    }
}