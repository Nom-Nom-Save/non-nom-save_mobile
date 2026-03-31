package ua.nure.nomnomsave.repository.establishment

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.mappers.toEntity
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.EstablishmentDetailDto
import ua.nure.nomnomsave.repository.dto.EstablishmentDetailPrivateDto
import ua.nure.nomnomsave.repository.dto.EstablishmentListResponse
import ua.nure.nomnomsave.repository.dto.EstablishmentResponse
import ua.nure.nomnomsave.repository.dto.UpdateEstablishmentInput
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.safeCall

@OptIn(ExperimentalCoroutinesApi::class)
class EstablishmentRepositoryImpl(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @param:DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : EstablishmentRepository {

    override suspend fun getAllEstablishments(): Result<List<EstablishmentDetailDto>, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentListResponse> {
                httpClient.get("establishments")
            }.let { result ->
                when (result) {
                    is Result.Success -> {
                        val establishments = result.data.establishments
                        dbRepository.db.establishedDao.insert(
                            establishments.map { it.toEntity() }
                        )
                        Result.Success(establishments)
                    }
                    is Result.Error -> result
                }
            }
        }

    override suspend fun getEstablishmentsByCity(city: String): Result<List<EstablishmentDetailDto>, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentListResponse> {
                httpClient.get("establishments?city=$city")
            }.let { result ->
                when (result) {
                    is Result.Success -> {
                        val establishments = result.data.establishments
                        dbRepository.db.establishedDao.insert(
                            establishments.map { it.toEntity() }
                        )
                        Result.Success(establishments)
                    }
                    is Result.Error -> result
                }
            }
        }

    override suspend fun getEstablishmentsByRadius(
        lat: Double,
        lon: Double,
        radiusKm: Double,
    ): Result<List<EstablishmentDetailDto>, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentListResponse> {
                httpClient.get("establishments/nearby?lat=$lat&lon=$lon&radius=$radiusKm")
            }.let { result ->
                when (result) {
                    is Result.Success -> {
                        val establishments = result.data.establishments
                        dbRepository.db.establishedDao.insert(
                            establishments.map { it.toEntity() }
                        )
                        Result.Success(establishments)
                    }
                    is Result.Error -> result
                }
            }
        }

    override suspend fun getEstablishmentById(id: String): Result<EstablishmentDetailDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentResponse> {
                httpClient.get("establishments/$id")
            }.let { result ->
                when (result) {
                    is Result.Success -> {
                        val establishment = result.data.establishment
                        if (establishment != null) {
                            dbRepository.db.establishedDao.insertOne(establishment.toEntity())
                            Result.Success(EstablishmentDetailDto(
                                id = establishment.id,
                                email = establishment.email,
                                name = establishment.name,
                                description = establishment.description,
                                address = establishment.address,
                                latitude = establishment.latitude,
                                longitude = establishment.longitude,
                                workingHours = establishment.workingHours,
                                logo = establishment.logo,
                                banner = establishment.banner,
                                rating = establishment.rating,
                                createdAt = establishment.createdAt,
                                isEmailVerified = establishment.isEmailVerified,
                                status = establishment.status
                            ))
                        } else {
                            Result.Error(DataError.Remote.UNKNOWN)
                        }
                    }
                    is Result.Error -> result
                }
            }
        }

    override suspend fun getEstablishmentPrivate(): Result<EstablishmentDetailPrivateDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentResponse> {
                httpClient.get("establishments/profile")
            }.let { result ->
                when (result) {
                    is Result.Success -> {
                        val establishment = result.data.establishment
                        if (establishment != null) {
                            dbRepository.db.establishedDao.insertOne(establishment.toEntity())
                            Result.Success(establishment)
                        } else {
                            Result.Error(DataError.Remote.UNKNOWN)
                        }
                    }
                    is Result.Error -> result
                }
            }
        }

    override suspend fun updateEstablishment(
        updateData: UpdateEstablishmentInput,
    ): Result<EstablishmentDetailPrivateDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentResponse> {
                httpClient.patch("establishments") {
                    setBody(updateData)
                }
            }.let { result ->
                when (result) {
                    is Result.Success -> {
                        val establishment = result.data.establishment
                        if (establishment != null) {
                            dbRepository.db.establishedDao.insertOne(establishment.toEntity())
                            Result.Success(establishment)
                        } else {
                            Result.Error(DataError.Remote.UNKNOWN)
                        }
                    }
                    is Result.Error -> result
                }
            }
        }

    override fun getAllEstablishmentsFlow(): Flow<List<EstablishmentEntity>> =
        dbRepository.db.establishedDao.getAllFlow()
            .flowOn(dbDeliveryDispatcher)
            .catch { emit(emptyList()) }

    override fun getEstablishmentByIdFlow(id: String): Flow<EstablishmentEntity?> =
        dbRepository.db.establishedDao.getByIdFlow(id)
            .flowOn(dbDeliveryDispatcher)
            .catch { emit(null) }

    override suspend fun refreshEstablishments(): Unit = withContext(Dispatchers.IO) {
        getAllEstablishments()
    }
    override suspend fun clearCache(): Unit = withContext(Dispatchers.IO) {
        dbRepository.db.establishedDao.deleteAll()
    }
}



