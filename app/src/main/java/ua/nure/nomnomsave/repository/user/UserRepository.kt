package ua.nure.nomnomsave.repository.user

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.Favorite
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.FavoritesResponseDto
import ua.nure.nomnomsave.repository.dto.ResponseDto

interface UserRepository {
    suspend fun favorites(): Result<FavoritesResponseDto, DataError>
    fun getFavorites(): Flow<List<Favorite>>
    suspend fun deleteFromFavorites(favoriteId: String, establishmentId: String): Result<ResponseDto, DataError>
    suspend fun addToFavorites(establishmentId: String): Result<ResponseDto, DataError>


}