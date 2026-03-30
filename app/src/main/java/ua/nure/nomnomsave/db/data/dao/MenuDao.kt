package ua.nure.nomnomsave.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.MenuEntity

@Dao
interface MenuDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<MenuEntity>)

    @Query("SELECT * FROM MenuEntity WHERE id = :id")
    fun getById(id: String): Flow<MenuEntity>

    @Query("SELECT * FROM MenuEntity WHERE establishmentId = :establishmentId")
    fun getByEstablishmentId(establishmentId: String): Flow<List<MenuEntity>>
}