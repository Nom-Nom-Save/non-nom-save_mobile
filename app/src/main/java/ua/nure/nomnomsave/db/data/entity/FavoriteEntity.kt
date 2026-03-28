package ua.nure.nomnomsave.db.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ua.nure.nomnomsave.repository.dto.EstablishmentDto
import java.time.LocalDateTime

@Entity
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val establishmentId: String,
    val createdAt: LocalDateTime,
)

data class Favorite(
    @Embedded val favoriteEntity: FavoriteEntity,
    @Relation(
        entity = EstablishmentEntity::class,
        parentColumn = "establishmentId",
        entityColumn = "id"
    ) val establishment: EstablishmentEntity
)
