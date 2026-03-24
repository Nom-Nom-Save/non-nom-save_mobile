package ua.nure.nomnomsave.repository.dto.mapper

import ua.nure.nomnomsave.db.data.entity.ProfileEntity
import ua.nure.nomnomsave.repository.dto.ProfileDto

fun ProfileDto.toEntity() =
    ProfileEntity(
        id = id,
        email = email,
        fullName = fullName,
        avatarUrl = avatarUrl,
        isEmailVerified = isEmailVerified,
        createdAt = createdAt,
    )