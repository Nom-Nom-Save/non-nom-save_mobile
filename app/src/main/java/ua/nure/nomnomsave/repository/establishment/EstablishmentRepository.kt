package ua.nure.nomnomsave.repository.establishment

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.EstablishmentDetailDto
import ua.nure.nomnomsave.repository.dto.EstablishmentDetailPrivateDto
import ua.nure.nomnomsave.repository.dto.UpdateEstablishmentInput

interface EstablishmentRepository {
    // Remote operations
    suspend fun getAllEstablishments(): Result<List<EstablishmentDetailDto>, DataError>

    suspend fun getEstablishmentsByCity(city: String): Result<List<EstablishmentDetailDto>, DataError>

    suspend fun getEstablishmentsByRadius(
        lat: Double,
        lon: Double,
        radiusKm: Double,
    ): Result<List<EstablishmentDetailDto>, DataError>

    suspend fun getEstablishmentById(id: String): Result<EstablishmentDetailDto, DataError>

    suspend fun getEstablishmentPrivate(): Result<EstablishmentDetailPrivateDto, DataError>

    suspend fun updateEstablishment(
        updateData: UpdateEstablishmentInput,
    ): Result<EstablishmentDetailPrivateDto, DataError>

    // Local operations
    fun getAllEstablishmentsFlow(): Flow<List<EstablishmentEntity>>

    fun getEstablishmentByIdFlow(id: String): Flow<EstablishmentEntity?>

    suspend fun refreshEstablishments()

    suspend fun clearCache()
}


