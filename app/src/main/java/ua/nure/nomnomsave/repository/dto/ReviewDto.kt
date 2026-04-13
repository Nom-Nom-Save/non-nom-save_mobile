package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: String,
    val rating: Int,
    val comment: String,
    val createdAt: String,
    val user: ReviewUserDto? = null,
    val editableUntil: String? = null,
    val isEditable: Boolean? = null,
    val establishmentId: String? = null,
    val userId: String? = null
)

@Serializable
data class ReviewUserDto(
    val id: String,
    val fullName: String
)

@Serializable
data class RatingDistributionDto(
    val rating: Int,
    val count: Int,
    val percentage: Double
)

@Serializable
data class MetaDto(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)

@Serializable
data class UpdatedEstablishmentDto(
    val id: String,
    val name: String,
    val description: String,
    val address: String,
    val latitude: String,
    val longitude: String,
    val workingHours: String,
    val logo: String? = null,
    val banner: String? = null,
    val rating: String,
    val createdAt: String
)