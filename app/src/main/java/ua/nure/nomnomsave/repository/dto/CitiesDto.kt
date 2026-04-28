package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class CitiesResponse(
    val cities: List<String>,
)

