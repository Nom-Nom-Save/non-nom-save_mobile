package ua.nure.nomnomsave.repository.review.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewRequest(
    val establishmentId: String,
    val rating: Int,
    val comment: String
)
