package ua.nure.nomnomsave.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EstablishmentEntity (
    @PrimaryKey val id: String,
    val email: String? = null,
    val name: String? = null,
    val description: String? = null,
    val adress: String? = null,
    val latitude: String? = null,
    val longtitude: String? = null,
    val workingHours: String? = null,
    val logo: String? = null,
    val banner: String? = null,
    val rating: String? = null,
    val boundTo: String? = null,
    val isEmailVerified: Boolean,
    val createdAt: String? = null,
    val status: String? = null,
)