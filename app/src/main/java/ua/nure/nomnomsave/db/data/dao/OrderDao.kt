package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.db.data.entity.OrderEntity

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<OrderEntity>)

    @Query("SELECT * FROM OrderEntity")
    fun get(): Flow<List<Order>>

}