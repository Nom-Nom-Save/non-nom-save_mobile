package ua.nure.nomnomsave.repository.review.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReviewRequest(
    val rating: Int,
    val comment: String
)
