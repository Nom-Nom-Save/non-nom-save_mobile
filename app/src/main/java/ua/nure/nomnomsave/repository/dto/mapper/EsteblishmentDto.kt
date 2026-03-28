package ua.nure.nomnomsave.repository.dto.mapper

import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.repository.dto.EstablishmentDto

fun EstablishmentDto.toEntity(id: String) =
    EstablishmentEntity(
        id = id,
        name = name,
        adress = address,
        logo = logo,
        banner = banner,
        rating = rating,
        isEmailVerified = false
    )