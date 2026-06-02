package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ua.nure.nomnomsave.db.data.entity.OrderDetailsEntity

@Dao
interface OrderDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<OrderDetailsEntity>)

    @Query("DELETE FROM ORDERDETAILSENTITY")
    suspend fun clear()
}