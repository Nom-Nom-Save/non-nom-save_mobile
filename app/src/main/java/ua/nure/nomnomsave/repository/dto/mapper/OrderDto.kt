package ua.nure.nomnomsave.repository.dto.mapper

import ua.nure.nomnomsave.db.data.entity.OrderEntity
import ua.nure.nomnomsave.extention.toLocalDateTime
import ua.nure.nomnomsave.repository.dto.OrderDto

fun OrderDto.toEntity() =
    OrderEntity(
        id = id,
        userId = userId,
        totalPrice = totalPrice,
        orderStatus = orderStatus,
        qrCodeData = qrCodeData,
        reservedAt = reservedAt?.toLocalDateTime(),
        expiresAt = expiresAt?.toLocalDateTime(),
        completedAt = completedAt?.toLocalDateTime(),
        establishmentName = establishmentName,
        establishmentAddress = establishmentAddress,
        establishmentLogo = establishmentLogo,
        establishmentBanner = establishmentBanner,
        allergens = allergens.isNotEmpty(),
        totalOrderWeight = totalOrderWeight
    )