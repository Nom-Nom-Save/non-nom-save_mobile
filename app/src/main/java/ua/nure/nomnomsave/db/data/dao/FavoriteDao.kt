package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.Favorite
import ua.nure.nomnomsave.db.data.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<FavoriteEntity>)

    @Query("SELECT * FROM FavoriteEntity")
    fun get(): Flow<List<Favorite>>

    @Query("DELETE FrOM FavoriteEntity WHERE id = :favoriteId")
    suspend fun deleteByFavoriteId(favoriteId: String)
}