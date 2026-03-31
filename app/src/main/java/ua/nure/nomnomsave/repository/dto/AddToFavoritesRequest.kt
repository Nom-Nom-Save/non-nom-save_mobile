package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddToFavoritesRequest(
    val establishmentId: String
)
