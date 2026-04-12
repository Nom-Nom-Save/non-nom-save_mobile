package ua.nure.nomnomsave.db.data.mappers

import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.repository.dto.EstablishmentDetailDto
import ua.nure.nomnomsave.repository.dto.EstablishmentDetailPrivateDto

fun EstablishmentDetailDto.toEntity(): EstablishmentEntity =
    EstablishmentEntity(
        id = this.id,
        email = this.email,
        name = this.name,
        description = this.description,
        adress = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        workingHours = this.workingHours,
        logo = this.logo,
        banner = this.banner,
        rating = this.rating,
        boundTo = null,
        isEmailVerified = this.isEmailVerified,
        createdAt = this.createdAt,
        status = this.status,
    )

fun EstablishmentDetailPrivateDto.toEntity(): EstablishmentEntity =
    EstablishmentEntity(
        id = this.id,
        email = this.email,
        name = this.name,
        description = this.description,
        adress = this.address,
        latitude = this.latitude,
        longitude = this.longitude,
        workingHours = this.workingHours,
        logo = this.logo,
        banner = this.banner,
        rating = this.rating,
        boundTo = this.boundTo,
        isEmailVerified = this.isEmailVerified,
        createdAt = this.createdAt,
        status = this.status,
    )