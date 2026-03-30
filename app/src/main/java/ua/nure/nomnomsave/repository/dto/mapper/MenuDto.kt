package ua.nure.nomnomsave.repository.dto.mapper

import ua.nure.nomnomsave.db.data.entity.ItemDetailsEntity
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.db.data.entity.PriceDataEntity
import ua.nure.nomnomsave.repository.dto.MenuDto

fun MenuDto.toEntity(): MenuEntity {
    return MenuEntity(
        id = this.id,
        establishmentId = this.establishmentId,
        itemId = this.itemId,
        itemType = this.itemType,
        status = this.status,
        priceData = this.priceData?.let {
            PriceDataEntity(
                id = it.id,
                menuItemId = it.menuItemId,
                totalQuantity = it.totalQuantity,
                availableQuantity = it.availableQuantity,
                originalPrice = it.originalPrice,
                discountPrice = it.discountPrice,
                startTime = it.startTime,
                endTime = it.endTime,
                createdAt = it.createdAt
            )
        },
        itemDetails = this.itemDetails?.let {
            ItemDetailsEntity(
                name = it.name,
                description = it.description,
                picture = it.picture,
                weightInfo = it.weightInfo,
                types = it.types,
                allergens = it.allergens
            )
        }
    )
}