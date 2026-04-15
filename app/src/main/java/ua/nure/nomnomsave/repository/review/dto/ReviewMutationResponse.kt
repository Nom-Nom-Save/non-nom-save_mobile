package ua.nure.nomnomsave.repository.review.dto

import kotlinx.serialization.Serializable
import ua.nure.nomnomsave.repository.dto.ReviewDto
import ua.nure.nomnomsave.repository.dto.UpdatedEstablishmentDto

@Serializable
data class ReviewMutationResponse(
    val message: String,
    val review: ReviewDto? = null,
    val updatedEstablishment: UpdatedEstablishmentDto
)