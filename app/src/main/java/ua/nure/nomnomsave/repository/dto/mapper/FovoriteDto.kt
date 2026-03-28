package ua.nure.nomnomsave.repository.dto.mapper

import ua.nure.nomnomsave.db.data.dao.FavoriteDao
import ua.nure.nomnomsave.db.data.entity.Favorite
import ua.nure.nomnomsave.db.data.entity.FavoriteEntity
import ua.nure.nomnomsave.extention.toLocalDateTime
import ua.nure.nomnomsave.repository.dto.FavoriteDto

fun FavoriteDto.toEntity() =
    FavoriteEntity(
        id = id,
        userId = userId,
        establishmentId = establishmentId,
        createdAt = addedAt.toLocalDateTime()
    )