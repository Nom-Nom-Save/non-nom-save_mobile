package ua.nure.nomnomsave.repository.review

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.ReviewEntity
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.review.dto.GetReviewsResponse
import ua.nure.nomnomsave.repository.review.dto.RatingDistributionResponse
import ua.nure.nomnomsave.repository.review.dto.ReviewMutationResponse

interface ReviewRepository {
    suspend fun getLocalReviews(establishmentId: String): Flow<List<ReviewEntity>>

    suspend fun fetchReviews(establishmentId: String, page: Int = 1, limit: Int = 10): Result<GetReviewsResponse, DataError>

    suspend fun fetchRatingDistribution(establishmentId: String): Result<RatingDistributionResponse, DataError>

    suspend fun createReview(establishmentId: String, rating: Int, comment: String): Result<ReviewMutationResponse, DataError>

    suspend fun updateReview(reviewId: String, rating: Int, comment: String): Result<ReviewMutationResponse, DataError>

    suspend fun deleteReview(reviewId: String): Result<ReviewMutationResponse, DataError>
}