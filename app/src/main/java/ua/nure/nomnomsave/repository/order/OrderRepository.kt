package ua.nure.nomnomsave.repository.order

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.db.data.entity.OrderEntity
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.CreateOrderRequest
import ua.nure.nomnomsave.repository.dto.OrderDto
import ua.nure.nomnomsave.repository.dto.OrdersResponseDto
import ua.nure.nomnomsave.repository.dto.UpdateOrderStatusRequest

interface OrderRepository {
    suspend fun orders(page: Int = 1, limit: Int = 10): Result<OrdersResponseDto, DataError>
    suspend fun getOrders(): Flow<List<Order>>
    suspend fun getOrderById(id: String): Result<OrderDto, DataError>
    suspend fun createOrder(request: CreateOrderRequest): Result<OrderDto, DataError>
    suspend fun updateOrderStatus(id: String, request: UpdateOrderStatusRequest): Result<OrderDto, DataError>
    suspend fun cancelOrder(id: String): Result<OrderDto, DataError>
}