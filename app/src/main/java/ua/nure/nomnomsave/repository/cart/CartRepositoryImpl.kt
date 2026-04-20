package ua.nure.nomnomsave.repository.cart

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.db.data.entity.CartItemEntity
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.CreateOrderRequest
import ua.nure.nomnomsave.repository.dto.OrderDto
import ua.nure.nomnomsave.repository.dto.OrderItemRequest
import ua.nure.nomnomsave.repository.dto.OrderResponseWrapper
import ua.nure.nomnomsave.repository.safeCall
import kotlinx.coroutines.CloseableCoroutineDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class CartRepositoryImpl(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    @param:DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
) : CartRepository {

    override suspend fun addToCart(item: CartItemEntity): Result<Unit, DataError> =
        withContext(Dispatchers.IO) {
            try {
                val existingItem = dbRepository.db.cartItemDao.getItemByMenuPriceId(item.menuPriceId)
                if (existingItem != null) {
                    dbRepository.db.cartItemDao.updateQuantity(
                        existingItem.id,
                        existingItem.quantity + item.quantity
                    )
                } else {
                    dbRepository.db.cartItemDao.insert(item)
                }
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        }

    override suspend fun removeFromCart(id: String): Result<Unit, DataError> =
        withContext(Dispatchers.IO) {
            try {
                dbRepository.db.cartItemDao.deleteById(id)
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        }

    override suspend fun updateQuantity(id: String, quantity: Int): Result<Unit, DataError> =
        withContext(Dispatchers.IO) {
            try {
                if (quantity <= 0) {
                    dbRepository.db.cartItemDao.deleteById(id)
                } else {
                    dbRepository.db.cartItemDao.updateQuantity(id, quantity)
                }
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        }

    override suspend fun clearCart(): Result<Unit, DataError> =
        withContext(Dispatchers.IO) {
            try {
                dbRepository.db.cartItemDao.clear()
                Result.Success(Unit)
            } catch (_: Exception) {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        }

    override fun getCartItems(): Flow<List<CartItemEntity>> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.cartItemDao.getAllItems()
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)

    override fun getItemCount(): Flow<Int> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.cartItemDao.getItemCount()
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)

    override fun getTotalQuantity(): Flow<Int?> =
        dbRepository.dbFlow.flatMapLatest { db ->
            db.cartItemDao.getTotalQuantity()
        }.catch { ex -> ex.printStackTrace() }
            .flowOn(dbDeliveryDispatcher)

    override suspend fun getTotalPrice(): Double =
        withContext(Dispatchers.IO) {
            try {
                val items = dbRepository.db.cartItemDao.getAllItems()
                var total = 0.0
                items.collect { cartItems ->
                    total = cartItems.sumOf { item ->
                        val price = item.discountPrice ?: item.price
                        price * item.quantity
                    }
                }
                total
            } catch (_: Exception) {
                0.0
            }
        }

    override suspend fun createOrderFromCart(): Result<OrderDto, DataError> =
        withContext(Dispatchers.IO) {
            try {
                val cartItemsFlow = dbRepository.db.cartItemDao.getAllItems()
                var items = emptyList<CartItemEntity>()
                cartItemsFlow.collect { items = it }

                if (items.isEmpty()) {
                    return@withContext Result.Error(DataError.Remote.UNKNOWN)
                }

                val orderItems = items.map { item ->
                    OrderItemRequest(
                        menuPriceId = item.menuPriceId,
                        quantity = item.quantity
                    )
                }

                val createOrderRequest = CreateOrderRequest(items = orderItems)

                val result = safeCall<OrderResponseWrapper> {
                    httpClient.post("orders") {
                        setBody(createOrderRequest)
                    }
                }

                when (result) {
                    is Result.Success -> {
                        dbRepository.db.cartItemDao.clear()
                        Result.Success(result.data.order)
                    }
                    is Result.Error -> result
                }
            } catch (_: Exception) {
                Result.Error(DataError.Remote.UNKNOWN)
            }
        }

    override suspend fun getCartItemByMenuPriceId(menuPriceId: String): CartItemEntity? =
        withContext(Dispatchers.IO) {
            try {
                dbRepository.db.cartItemDao.getItemByMenuPriceId(menuPriceId)
            } catch (_: Exception) {
                null
            }
        }
}



