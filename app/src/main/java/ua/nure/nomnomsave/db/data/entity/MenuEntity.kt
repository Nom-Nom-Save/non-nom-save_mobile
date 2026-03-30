package ua.nure.nomnomsave.db.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

@Entity
data class MenuEntity(
    @PrimaryKey val id: String,
    val establishmentId: String? = null,
    val itemId: String? = null,
    val itemType: String? = null,
    val status: String? = null,
    @Embedded(prefix = "price_")
    val priceData: PriceDataEntity? = null,
    @Embedded(prefix = "details_")
    val itemDetails: ItemDetailsEntity? = null
)

data class PriceDataEntity(
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

data class ItemDetailsEntity(
    val name: String? = null,
    val description: String? = null,
    val picture: String? = null,
    val weightInfo: String? = null,
    val types: List<String>? = null,
    val allergens: List<String>? = null
)
