package ua.nure.nomnomsave.repository.order

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.OrderDto
import ua.nure.nomnomsave.repository.dto.OrdersResponseDto
import ua.nure.nomnomsave.repository.dto.mapper.toEntity
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.safeCall

class OrderRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : OrderRepository {
    override suspend fun orders(): Result<OrdersResponseDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<OrdersResponseDto> {
                httpClient.get("orders") {

                }
            }.onSuccess {
                dbRepository.db.orderDao.insert(it.orders.map { it.toEntity() })
                dbRepository.db.orderDetailsDao.insert(
                    it.orders
                        .flatMap { it.details }
                        .map { it.toEntity() }
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getOrders(): Flow<List<Order>> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.orderDao.get()
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)
}