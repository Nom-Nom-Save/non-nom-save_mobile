package ua.nure.nomnomsave.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderDetailsEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val menuPriceId: String,
    val quantity: Int,
    val price: Double,
    val originalPrice: Double,
    val discountPrice: Double,
    val itemName: String,
    val itemType: String,
    val itemPicture: String? = null,
    val weight: Int,
    val minWeight: Int? = null,
    val maxWeight: Int? = null
)
