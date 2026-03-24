package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProfileEntity)

    @Query("SELECT * FROM profileentity WHERE isOwned == true")
    fun getProfileEntity(): Flow<ProfileEntity?>
}