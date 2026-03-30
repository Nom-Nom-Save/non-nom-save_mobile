package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class MenuResponseDto(
    val menu: List<MenuDto>
)

@Serializable
data class MenuDto(
    val id: String,
    val establishmentId: String? = null,
    val itemId: String? = null,
    val itemType: String? = null,
    val status: String? = null,
    val priceData: PriceDataDto? = null,
    val itemDetails: ItemDetailsDto? = null
)

@Serializable
data class PriceDataDto(
    val id: String? = null,
    val menuItemId: String? = null,
    val totalQuantity: Int? = null,
    val availableQuantity: Int? = null,
    val originalPrice: Double? = null,
    val discountPrice: Double? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val createdAt: String? = null
)

@Serializable
data class ItemDetailsDto(
    val name: String? = null,
    val description: String? = null,
    val picture: String? = null,
    val weightInfo: String? = null,
    val types: List<String>? = null,
    val allergens: List<String>? = null
)