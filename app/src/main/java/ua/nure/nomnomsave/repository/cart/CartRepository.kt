package ua.nure.nomnomsave.repository.cart

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.CartItemEntity
import ua.nure.nomnomsave.repository.dto.OrderDto
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result

interface CartRepository {
    suspend fun addToCart(item: CartItemEntity): Result<Unit, DataError>
    suspend fun removeFromCart(id: String): Result<Unit, DataError>
    suspend fun updateQuantity(id: String, quantity: Int): Result<Unit, DataError>
    suspend fun clearCart(): Result<Unit, DataError>
    fun getCartItems(): Flow<List<CartItemEntity>>
    fun getItemCount(): Flow<Int>
    fun getTotalQuantity(): Flow<Int?>
    suspend fun getTotalPrice(): Double
    suspend fun createOrderFromCart(): Result<OrderDto, DataError>
    suspend fun getCartItemByMenuPriceId(menuPriceId: String): CartItemEntity?
}


