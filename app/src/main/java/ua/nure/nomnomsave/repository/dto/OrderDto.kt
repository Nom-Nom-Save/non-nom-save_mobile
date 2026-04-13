package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class OrdersResponseDto(
    val orders: List<OrderDto>,
    val meta: PaginationMeta? = null
)

@Serializable
data class PaginationMeta(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)

@Serializable
data class OrderDto(
    val id: String,
    val userId: String,
    val totalPrice: Double,
    val orderStatus: OrderStatus,
    val qrCodeData: String,
    val reservedAt: String? = null,
    val expiresAt: String? = null,
    val completedAt: String? = null,
    val details: List<OrderDetailDto>,
    val establishmentName: String,
    val establishmentAddress: String,
    val establishmentLogo: String,
    val establishmentBanner: String? = null,
    val allergens: List<String>,
    val totalOrderWeight: Int
)

@Serializable
data class OrderDetailDto(
    val id: String,
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

@Serializable(with = OrderStatusSerializer::class)
enum class OrderStatus {
    Reserved,
    Completed,
    Cancelled,
    Expired
}

object OrderStatusSerializer : KSerializer<OrderStatus> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("OrderStatus", kind = PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: OrderStatus
    ) {
        encoder.encodeString(
            when (value) {
                OrderStatus.Reserved -> "Reserved"
                OrderStatus.Completed -> "Completed"
                OrderStatus.Cancelled -> "Cancelled"
                OrderStatus.Expired -> "Expired"
            }
        )
    }

    override fun deserialize(decoder: Decoder): OrderStatus =
        when (val result = decoder.decodeString()) {
            "Reserved" -> OrderStatus.Reserved
            "Completed" -> OrderStatus.Completed
            "Cancelled" -> OrderStatus.Cancelled
            "Expired" -> OrderStatus.Expired
            else -> throw IllegalArgumentException("OrderStatus $result Not found")
        }
}