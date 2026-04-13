package ua.nure.nomnomsave.repository.review.dto

import kotlinx.serialization.Serializable
import ua.nure.nomnomsave.repository.dto.MetaDto
import ua.nure.nomnomsave.repository.dto.ReviewDto

@Serializable
data class GetReviewsResponse(
    val reviews: List<ReviewDto> = emptyList(),
    val myReview: ReviewDto? = null,
    val meta: MetaDto? = null
)