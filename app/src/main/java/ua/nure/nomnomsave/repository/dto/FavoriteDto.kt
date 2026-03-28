package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class FavoritesResponseDto(
    val favorites: List<FavoriteDto>
)

@Serializable
data class FavoriteDto(
    val id: String,
    val userId: String,
    val establishmentId: String,
    val addedAt: String,
    val establishment: EstablishmentDto
)

@Serializable
data class EstablishmentDto(
    val name: String,
    val address: String,
    val logo: String? = null,
    val banner: String? = null,
    val rating: String? = null,
)