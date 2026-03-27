package ua.nure.nomnomsave.repository.order

import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result

interface OrderRepository {
    suspend fun orders(): Result<Any, DataError>
}