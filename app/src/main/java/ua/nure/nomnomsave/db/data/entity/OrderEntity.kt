package ua.nure.nomnomsave.db.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import ua.nure.nomnomsave.repository.dto.OrderDetailDto
import ua.nure.nomnomsave.repository.dto.OrderStatus
import java.time.LocalDateTime

@Entity
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val totalPrice: Double,
    val orderStatus: OrderStatus,
    val qrCodeData: String,
    val reservedAt: LocalDateTime? =null,
    val expiresAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val establishmentName: String,
    val establishmentAddress: String,
    val establishmentLogo: String,
    val establishmentBanner: String? = null,
    val allergens: Boolean = false,
    val totalOrderWeight: Int
)

data class Order(
    @Embedded val orderEntity: OrderEntity,
    @Relation(
        entity = OrderDetailsEntity::class,
        parentColumn = "id",
        entityColumn = "orderId"
    ) val details: List<OrderDetailsEntity>
)