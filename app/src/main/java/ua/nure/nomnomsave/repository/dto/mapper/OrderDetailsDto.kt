package ua.nure.nomnomsave.repository.dto.mapper

import ua.nure.nomnomsave.db.data.entity.OrderDetailsEntity
import ua.nure.nomnomsave.repository.dto.OrderDetailDto

fun OrderDetailDto.toEntity() =
    OrderDetailsEntity(
        id = id,
        orderId = orderId,
        menuPriceId = menuPriceId,
        quantity = quantity,
        price = price,
        originalPrice = originalPrice,
        discountPrice = discountPrice,
        itemName = itemName,
        itemType = itemType,
        weight = weight,
        minWeight = minWeight,
        maxWeight = maxWeight
    )