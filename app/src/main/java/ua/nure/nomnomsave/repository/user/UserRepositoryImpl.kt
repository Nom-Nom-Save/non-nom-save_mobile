package ua.nure.nomnomsave.repository.user

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
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
import ua.nure.nomnomsave.db.data.entity.Favorite
import ua.nure.nomnomsave.db.data.entity.FavoriteEntity
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.AddToFavoritesRequest
import ua.nure.nomnomsave.repository.dto.FavoritesResponseDto
import ua.nure.nomnomsave.repository.dto.ResponseDto
import ua.nure.nomnomsave.repository.dto.mapper.toEntity
import ua.nure.nomnomsave.repository.onError
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.safeCall

class UserRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : UserRepository {
    private val TAG by lazy { UserRepositoryImpl::class.simpleName }

    override suspend fun favorites(): Result<FavoritesResponseDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<FavoritesResponseDto> {
                httpClient.get("users/favorites") {

                }
            }.onSuccess { dto ->
                val est = dto.favorites.map {
                    it.establishmentId to it.establishment
                }.map { (establishmentId, item) ->
                    item.toEntity(establishmentId)
                }

                Log.d(TAG, "favorites: $est")

                dbRepository.db.favoriteDao.insert(
                    dto.favorites.map {
                        it.toEntity()
                    }
                )
                dbRepository.db.establishedDao.insert(
                    dto.favorites.map {
                        it.establishmentId to it.establishment
                    }.map { (establishmentId, item) ->
                        item.toEntity(establishmentId)
                    }
                )
            }.onError {
                Log.e(TAG, "favorites: $it")
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFavorites(): Flow<List<Favorite>> =
        dbRepository.dbFlow
            .flatMapLatest { db -> db.favoriteDao.get() }
            .catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)

    override suspend fun deleteFromFavorites(favoriteId: String, establishmentId: String): Result<ResponseDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<ResponseDto> {
                httpClient.delete("users/favorites/$establishmentId") {

                }
            }.onSuccess {
                dbRepository.db.favoriteDao.deleteByFavoriteId(favoriteId = favoriteId)
            }
        }

    override suspend fun addToFavorites(establishmentId: String): Result<ResponseDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<ResponseDto> {
                httpClient.post("users/favorites") {
                    setBody(
                        AddToFavoritesRequest(
                            establishmentId = establishmentId
                        )
                    )
                }
            }.onSuccess {
//                dbRepository.db.profileDao.getProfileEntity().firstOrNull()?.let { profile ->
//                    dbRepository.db.favoriteDao.insert(
//                        listOf(
//                            FavoriteEntity(
//                                id = XXX,
//                                userId = profile.id,
//                                establishmentId = establishmentId,
//                                createdAt = LocalDateTime.now()
//                            )
//                        )
//                    )
//
//                }
            }
    }

    override suspend fun getProfile(): Result<ua.nure.nomnomsave.repository.dto.ProfileDataDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<ua.nure.nomnomsave.repository.dto.ProfileDataDto> {
                httpClient.get("users/me") {

}