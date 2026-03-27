package ua.nure.nomnomsave.repository.order

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.safeCall

class OrderRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : OrderRepository {
    override suspend fun orders(): Result<Any, DataError> = withContext(Dispatchers.IO) {
        safeCall<Any> {
            httpClient.get("orders") {

            }
        }
    }
}