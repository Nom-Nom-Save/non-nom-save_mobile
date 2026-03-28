package ua.nure.nomnomsave.repository.order

import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.OrderDto

interface OrderRepository {
    suspend fun orders(): Result<OrderDto, DataError>
}