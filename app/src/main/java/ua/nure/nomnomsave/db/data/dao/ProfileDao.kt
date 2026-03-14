package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ua.nure.nomnomsave.db.data.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProfileEntity)
}