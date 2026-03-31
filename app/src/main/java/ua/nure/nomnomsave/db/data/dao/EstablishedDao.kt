package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity

@Dao
interface EstablishedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<EstablishmentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(entity: EstablishmentEntity)

    @Query("SELECT * FROM EstablishmentEntity")
    suspend fun getAll(): List<EstablishmentEntity>

    @Query("SELECT * FROM EstablishmentEntity")
    fun getAllFlow(): Flow<List<EstablishmentEntity>>

    @Query("SELECT * FROM EstablishmentEntity WHERE id = :id")
    suspend fun getById(id: String): EstablishmentEntity?

    @Query("SELECT * FROM EstablishmentEntity WHERE id = :id")
    fun getByIdFlow(id: String): Flow<EstablishmentEntity?>

    @Query("SELECT * FROM EstablishmentEntity WHERE adress LIKE '%' || :city || '%'")
    suspend fun getByCity(city: String): List<EstablishmentEntity>

    @Update
    suspend fun update(entity: EstablishmentEntity)

    @Query("DELETE FROM EstablishmentEntity WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM EstablishmentEntity")
    suspend fun deleteAll()
}