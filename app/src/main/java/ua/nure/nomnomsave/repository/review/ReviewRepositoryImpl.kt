package ua.nure.nomnomsave.repository.review

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.db.data.entity.ReviewEntity
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.mapper.*
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.review.dto.CreateReviewRequest
import ua.nure.nomnomsave.repository.review.dto.GetReviewsResponse
import ua.nure.nomnomsave.repository.review.dto.RatingDistributionResponse
import ua.nure.nomnomsave.repository.review.dto.ReviewMutationResponse
import ua.nure.nomnomsave.repository.review.dto.UpdateReviewRequest
import ua.nure.nomnomsave.repository.safeCall

class ReviewRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : ReviewRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getLocalReviews(establishmentId: String): Flow<List<ReviewEntity>> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.reviewDao.getReviewsByEstablishment(establishmentId)
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)

    override suspend fun fetchReviews(
        establishmentId: String,
        page: Int,
        limit: Int
    ): Result<GetReviewsResponse, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<GetReviewsResponse> {
                httpClient.get("reviews/establishment/$establishmentId") {
                    parameter("page", page)
                    parameter("limit", limit)
                }
            }.onSuccess { response ->
                val entities = response.reviews.map { it.toEntity(establishmentId, isMyReview = false) }.toMutableList()

                response.myReview?.let { myReviewDto ->
                    entities.removeAll { it.id == myReviewDto.id }
                    // Ось тут ми прибрали forcedUserName, бо мапер тепер сам знає, що писати "You"
                    entities.add(myReviewDto.toEntity(
                        establishmentId = establishmentId,
                        isMyReview = true
                    ))
                }

                if (page == 1) {
                    dbRepository.db.reviewDao.clearReviewsByEstablishment(establishmentId)
                }
                if (entities.isNotEmpty()) {
                    dbRepository.db.reviewDao.insertReviews(entities)
                }
            }
        }

    override suspend fun fetchRatingDistribution(
        establishmentId: String
    ): Result<RatingDistributionResponse, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<RatingDistributionResponse> {
                httpClient.get("reviews/establishment/$establishmentId/distribution")
            }
        }

    override suspend fun createReview(
        establishmentId: String,
        rating: Int,
        comment: String
    ): Result<ReviewMutationResponse, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<ReviewMutationResponse> {
                httpClient.post("reviews") {
                    setBody(CreateReviewRequest(establishmentId, rating, comment))
                }
            }.onSuccess { response ->
                response.review?.let { newReview ->
                    dbRepository.db.reviewDao.insertReviews(
                        listOf(newReview.toEntity(establishmentId, isMyReview = true, forcedIsEditable = true))
                    )
                }
            }
        }

    override suspend fun updateReview(
        reviewId: String,
        rating: Int,
        comment: String
    ): Result<ReviewMutationResponse, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<ReviewMutationResponse> {
                httpClient.patch("reviews/$reviewId") {
                    setBody(UpdateReviewRequest(rating, comment))
                }
            }.onSuccess { response ->
                response.review?.let { updatedReview ->
                    val estId = updatedReview.establishmentId ?: ""
                    dbRepository.db.reviewDao.insertReviews(
                        listOf(updatedReview.toEntity(
                            establishmentId = estId,
                            isMyReview = true,
                            forcedIsEditable = true
                        ))
                    )
                }
            }
        }

    override suspend fun deleteReview(reviewId: String): Result<ReviewMutationResponse, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<ReviewMutationResponse> {
                httpClient.delete("reviews/$reviewId")
            }.onSuccess {
                dbRepository.db.reviewDao.deleteReviewById(reviewId)
            }
        }
}