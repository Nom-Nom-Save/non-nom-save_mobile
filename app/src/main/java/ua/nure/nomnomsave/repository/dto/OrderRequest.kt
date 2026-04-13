package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItemRequest>
)

@Serializable
data class OrderItemRequest(
    val menuPriceId: String,
    val quantity: Int
)

@Serializable
data class UpdateOrderStatusRequest(
    val status: OrderStatus
)

@Serializable
data class OrderResponseWrapper(
    val message: String,
    val order: OrderDto
)

@Serializable
data class OrdersResponseWrapper(
    val orders: List<OrderDto>,
    val meta: PaginationMeta? = null
)



