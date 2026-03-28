package ua.nure.nomnomsave.repository.order

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.db.data.entity.OrderEntity
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.OrderDto
import ua.nure.nomnomsave.repository.dto.OrdersResponseDto

interface OrderRepository {
    suspend fun orders(): Result<OrdersResponseDto, DataError>
    suspend fun getOrders(): Flow<List<Order>>
}