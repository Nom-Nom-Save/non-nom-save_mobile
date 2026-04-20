package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.CartItemEntity

@Dao
interface CartItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Suppress("UNUSED")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CartItemEntity>)

    @Query("SELECT * FROM CartItemEntity ORDER BY addedAt DESC")
    fun getAllItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM CartItemEntity WHERE menuPriceId = :menuPriceId")
    suspend fun getItemByMenuPriceId(menuPriceId: String): CartItemEntity?

    @Query("SELECT * FROM CartItemEntity WHERE id = :id")
    suspend fun getItemById(id: String): CartItemEntity?

    @Query("UPDATE CartItemEntity SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: String, quantity: Int)

    @Query("DELETE FROM CartItemEntity WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM CartItemEntity WHERE menuPriceId = :menuPriceId")
    suspend fun deleteByMenuPriceId(menuPriceId: String)

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("DELETE FROM CartItemEntity")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM CartItemEntity")
    fun getItemCount(): Flow<Int>

    @Query("SELECT SUM(quantity) FROM CartItemEntity")
    fun getTotalQuantity(): Flow<Int?>

    @Suppress("UNUSED")
    @Query("SELECT SUM(quantity * COALESCE(discountPrice, price)) FROM CartItemEntity")
    suspend fun calculateTotalPrice(): Double
}


