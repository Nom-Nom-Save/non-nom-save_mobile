package ua.nure.nomnomsave.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.ReviewEntity

@Dao
interface ReviewDao {
    @Query("SELECT * FROM ReviewEntity WHERE establishmentId = :establishmentId ORDER BY createdAt DESC")
    fun getReviewsByEstablishment(establishmentId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("DELETE FROM ReviewEntity WHERE id = :reviewId")
    suspend fun deleteReviewById(reviewId: String)

    @Query("DELETE FROM ReviewEntity WHERE establishmentId = :establishmentId")
    suspend fun clearReviewsByEstablishment(establishmentId: String)
}