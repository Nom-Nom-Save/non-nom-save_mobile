package ua.nure.nomnomsave.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReviewEntity(
    @PrimaryKey val id: String,
    val establishmentId: String,
    val userId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String,
    val isMyReview: Boolean = false,
    val isEditable: Boolean = false
)