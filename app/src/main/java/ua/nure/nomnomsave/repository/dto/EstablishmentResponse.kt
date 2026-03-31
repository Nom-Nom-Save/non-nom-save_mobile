package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class EstablishmentListResponse(
    val message: String,
    val establishments: List<EstablishmentDetailDto>
)

@Serializable
data class EstablishmentResponse(
    val message: String? = null,
    val establishment: EstablishmentDetailPrivateDto? = null
)

