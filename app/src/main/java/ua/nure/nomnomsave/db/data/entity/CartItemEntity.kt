package ua.nure.nomnomsave.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@Suppress("UNUSED")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val menuPriceId: String,
    val quantity: Int,
    val itemName: String,
    val itemType: String,
    val itemPicture: String? = null,
    val price: Double,
    val originalPrice: Double,
    val discountPrice: Double? = null,
    val weight: Int,
    val minWeight: Int? = null,
    val maxWeight: Int? = null,
    val establishmentId: String,
    val establishmentName: String,
    val addedAt: Long = System.currentTimeMillis()
)


