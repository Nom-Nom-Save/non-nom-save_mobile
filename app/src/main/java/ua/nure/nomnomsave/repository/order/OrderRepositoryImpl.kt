package ua.nure.nomnomsave.repository.order

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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
import ua.nure.nomnomsave.repository.dto.CreateOrderRequest
import ua.nure.nomnomsave.repository.dto.OrderDto
import ua.nure.nomnomsave.repository.dto.OrderResponseWrapper
import ua.nure.nomnomsave.repository.dto.OrdersResponseDto
import ua.nure.nomnomsave.repository.dto.UpdateOrderStatusRequest
import ua.nure.nomnomsave.repository.dto.mapper.toEntity
import ua.nure.nomnomsave.repository.map
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.safeCall

class OrderRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : OrderRepository {
    override suspend fun orders(page: Int, limit: Int): Result<OrdersResponseDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<OrdersResponseDto> {
                httpClient.get("orders") {
                    url.parameters.append("page", page.toString())
                    url.parameters.append("limit", limit.toString())
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

    override suspend fun getOrderById(id: String): Result<OrderDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<OrderResponseWrapper> {
                httpClient.get("orders/$id")
            }.map { it.order }
        }

    override suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<OrderResponseWrapper> {
                httpClient.post("orders") {
                    setBody(request)
                }
            }.map { it.order }
        }

    override suspend fun updateOrderStatus(
        id: String,
        request: UpdateOrderStatusRequest
    ): Result<OrderDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<OrderResponseWrapper> {
                httpClient.patch("orders/$id/status") {
                    setBody(request)
                }
            }.map { it.order }
        }

    override suspend fun cancelOrder(id: String): Result<OrderDto, DataError> =
        withContext(Dispatchers.IO) {
            safeCall<OrderResponseWrapper> {
                httpClient.patch("orders/$id/cancel")
            }.map { it.order }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getOrders(): Flow<List<Order>> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.orderDao.get()
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)
}