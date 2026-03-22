package ua.nure.nomnomsave.repository.profile

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.db.data.dao.ProfileDao
import ua.nure.nomnomsave.db.data.entity.ProfileEntity
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.ProfileDto
import ua.nure.nomnomsave.repository.dto.mapper.toEntity
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.profile.dto.ProfileRequest
import ua.nure.nomnomsave.repository.safeCall
import kotlin.math.exp

class ProfileRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : ProfileRepository {
    override suspend fun loadMe(): Result<ProfileDto, DataError> = withContext(Dispatchers.IO) {
        safeCall<ProfileDto> {
            httpClient.get("users/me")
        }.onSuccess { profileDto ->
            dbRepository.db.profileDao
                .insert(
                    profileDto.toEntity().copy(isOwned = true)
                )
        }
    }

    override suspend fun patchMe(
        fullName: String?,
        email: String?
    ): Result<ProfileDto, DataError> = withContext(Dispatchers.IO) {
        safeCall<ProfileDto> {
            httpClient.patch("users/me") {
                setBody(
                    ProfileRequest(
                        fullName = fullName,
                        email = email,
//                        avatarUrl = avatarUrl
                    )
                )
            }
        }.onSuccess { profileDto ->
            dbRepository.db.profileDao.insert(profileDto.toEntity())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMe(): Flow<ProfileEntity> =
        dbRepository
        .dbFlow
            .flatMapLatest { db -> db.profileDao.getProfileEntity() }
            .filterNotNull()
            .flowOn(dbDeliveryDispatcher)
            .catch { it.printStackTrace() }
}