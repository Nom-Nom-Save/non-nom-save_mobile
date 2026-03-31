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
            safeCall<List<EstablishmentDetailDto>> {
                httpClient.get("establishments")
            }.onSuccess { establishments ->
                dbRepository.db.establishedDao.insert(
                    establishments.map { it.toEntity() }
                )
            }
        }

    override suspend fun getEstablishmentsByCity(city: String): Result<List<EstablishmentDetailDto>, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<List<EstablishmentDetailDto>> {
                httpClient.get("establishments?city=$city")
            }.onSuccess { establishments ->
                dbRepository.db.establishedDao.insert(
                    establishments.map { it.toEntity() }
                )
            }
        }

    override suspend fun getEstablishmentsByRadius(
        lat: Double,
        lon: Double,
        radiusKm: Double,
    ): Result<List<EstablishmentDetailDto>, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<List<EstablishmentDetailDto>> {
                httpClient.get("establishments/nearby?lat=$lat&lon=$lon&radius=$radiusKm")
            }.onSuccess { establishments ->
                dbRepository.db.establishedDao.insert(
                    establishments.map { it.toEntity() }
                )
            }
        }

    override suspend fun getEstablishmentById(id: String): Result<EstablishmentDetailDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentDetailDto> {
                httpClient.get("establishments/$id")
            }.onSuccess { establishment ->
                dbRepository.db.establishedDao.insertOne(establishment.toEntity())
            }
        }

    override suspend fun getEstablishmentPrivate(): Result<EstablishmentDetailPrivateDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentDetailPrivateDto> {
                httpClient.get("establishments/profile")
            }.onSuccess { establishment ->
                dbRepository.db.establishedDao.insertOne(establishment.toEntity())
            }
        }

    override suspend fun updateEstablishment(
        updateData: UpdateEstablishmentInput,
    ): Result<EstablishmentDetailPrivateDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<EstablishmentDetailPrivateDto> {
                httpClient.patch("establishments") {
                    setBody(updateData)
                }
            }.onSuccess { establishment ->
                dbRepository.db.establishedDao.insertOne(establishment.toEntity())
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



