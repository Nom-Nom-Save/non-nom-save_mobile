package ua.nure.nomnomsave.repository.review.dto

import kotlinx.serialization.Serializable
import ua.nure.nomnomsave.repository.dto.RatingDistributionDto

@Serializable
data class RatingDistributionResponse(
    val rating: String,
    val ratingDistribution: List<RatingDistributionDto>
)