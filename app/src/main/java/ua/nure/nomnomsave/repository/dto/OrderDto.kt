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
    val orders: List<OrderDto>
)

@Serializable
data class OrderDto(
    val id: String,
    val userId: String,
    val totalPrice: Double,
    val orderStatus: OrderStatus,
    val qrCodeData: String,
    val reservedAt: String?,
    val expiresAt: String?,
    val completedAt: String?,
    val details: List<OrderDetailDto>,
    val establishmentName: String
)

@Serializable
data class OrderDetailDto(
    val id: String,
    val orderId: String,
    val menuPriceId: String,
    val quantity: Int,
    val price: Double,
    val itemName: String,
    val itemType: String
)

@Serializable(with = OrderStatusSerializer::class)
enum class OrderStatus {
    Reserved,
    Completed,
    Cancelled
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
            }
        )
    }

    override fun deserialize(decoder: Decoder): OrderStatus =
        when (val result = decoder.decodeString()) {
            "Reserved" -> OrderStatus.Reserved
            "Completed" -> OrderStatus.Completed
            "Cancelled" -> OrderStatus.Cancelled
            else -> throw IllegalArgumentException("OrderStatus $result Not found")
        }
}