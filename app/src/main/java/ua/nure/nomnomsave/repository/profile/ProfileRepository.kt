package ua.nure.nomnomsave.repository.profile

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.ProfileEntity
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.ProfileDto

interface ProfileRepository {
    suspend fun loadMe(): Result<ProfileDto, DataError>

    suspend fun patchMe(
        fullName: String? = null,
        email: String? = null,
        notifyNearby: Boolean? = null,
        notifyClosingSoon: Boolean? = null,
//        avatarUrl: String,
    ) : Result<ProfileDto, DataError>

    fun getMe(): Flow<ProfileEntity>
}