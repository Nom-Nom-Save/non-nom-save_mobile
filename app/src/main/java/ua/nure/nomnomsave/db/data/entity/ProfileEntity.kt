package ua.nure.nomnomsave.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileEntity (
    @PrimaryKey val id: String,
    val email: String? = null,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val notifyNearby: Boolean = false,
    val notifyClosingSoon: Boolean = false,
    val notifyNewItems: Boolean = false,
    val isEmailVerified: Boolean,
    val createdAt: String,
    val isOwned: Boolean = false,
)