package ua.nure.nomnomsave.repository.dto.mapper

import ua.nure.nomnomsave.db.data.entity.ReviewEntity
import ua.nure.nomnomsave.repository.dto.ReviewDto

fun ReviewDto.toEntity(
    establishmentId: String,
    isMyReview: Boolean = false,
    forcedIsEditable: Boolean? = null
): ReviewEntity {
    return ReviewEntity(
        id = this.id,
        establishmentId = this.establishmentId ?: establishmentId,
        userId = this.user?.id ?: this.userId ?: "",

        userName = if (isMyReview) "You" else (this.user?.fullName ?: ""),

        rating = this.rating,
        comment = this.comment,
        createdAt = this.createdAt,
        isMyReview = isMyReview,
        isEditable = forcedIsEditable ?: this.isEditable ?: false
    )
}