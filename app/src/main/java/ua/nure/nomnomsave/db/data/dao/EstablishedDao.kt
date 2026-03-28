package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity

@Dao
interface EstablishedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<EstablishmentEntity>)
}