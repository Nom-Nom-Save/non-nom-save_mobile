package ua.nure.nomnomsave.repository.menu

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.MenuDto
import ua.nure.nomnomsave.repository.dto.MenuResponseDto
import ua.nure.nomnomsave.repository.dto.mapper.toEntity
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.safeCall

class MenuRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : MenuRepository {

    override suspend fun fetchMenu(establishmentId: String): Result<MenuResponseDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<MenuResponseDto> {
                httpClient.get("menu/public/$establishmentId") {

                }
            }.onSuccess {
                dbRepository.db.menuDao.insert(it.menu.map { item -> item.toEntity() })
            }
        }

    override suspend fun fetchMenuItem(menuItemId: String): Result<MenuDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<MenuDto> {
                httpClient.get("menu/item/$menuItemId") {

                }
            }.onSuccess {
                dbRepository.db.menuDao.insert(listOf(it.toEntity()))
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getMenuByEstablishment(establishmentId: String): Flow<List<MenuEntity>> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.menuDao.getByEstablishmentId(establishmentId)
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getMenuItem(menuItemId: String): Flow<MenuEntity> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.menuDao.getById(menuItemId)
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)
}