package ua.nure.nomnomsave.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileEntity (
    @PrimaryKey val id: String,
)