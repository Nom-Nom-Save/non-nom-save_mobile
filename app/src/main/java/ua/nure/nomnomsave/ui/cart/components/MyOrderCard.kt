package ua.nure.nomnomsave.ui.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.db.data.entity.OrderDetailsEntity
import ua.nure.nomnomsave.db.data.entity.OrderEntity
import ua.nure.nomnomsave.repository.dto.OrderStatus
import ua.nure.nomnomsave.ui.theme.AppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MyOrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    onQR: () -> Unit = {},
    onDeleteItem: (itemId: String) -> Unit = {},
    onDeleteOrder: () -> Unit = {},
) {
    if (order.details.size == 1) {
        CompactMyOrderCard(
            modifier = modifier,
            order = order,
            onQR = onQR,
            onDeleteItem = onDeleteItem,
        )
    } else {
        ExpandedMyOrderCard(
            modifier = modifier,
            order = order,
            onQR = onQR,
            onDeleteItem = onDeleteItem,
        )
    }
}

@Composable
private fun CompactMyOrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    onQR: () -> Unit = {},
    onDeleteItem: (itemId: String) -> Unit = {},
) {
    val entity = order.orderEntity
    val firstItem = order.details.firstOrNull()
    val totalPrice = order.details.sumOf { it.price * it.quantity }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimension.small))
            .background(AppTheme.color.cardBackground)
    ) {
        // Item Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimension.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
        ) {
            AsyncImage(
                model = firstItem?.itemPicture,
                contentDescription = firstItem?.itemName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(AppTheme.dimension.extraSmall)),
                error = painterResource(R.drawable.placeholder_image),
                placeholder = painterResource(R.drawable.placeholder_image),
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = firstItem?.itemName.orEmpty(),
                        style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                    )
                    Text(
                        text = "${firstItem?.weight ?: 0} g",
                        style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                    )
                }

                entity.expiresAt?.let { expiresAt ->
                    val formatted = formatExpiresAt(expiresAt)
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .border(1.dp, AppTheme.color.grey, RoundedCornerShape(50.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        text = "Collect till $formatted",
                        style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entity.establishmentAddress,
                        style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    if (entity.orderStatus == OrderStatus.Reserved) {
                        IconButton(
                            modifier = Modifier.size(32.dp),
                            onClick = { firstItem?.id?.let { onDeleteItem(it) } }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.trash),
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Footer with QR and Price
        if (entity.orderStatus == OrderStatus.Reserved) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimension.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2D6A4F))
                        .clickable { onQR() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.qr_code),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = String.format("$%.0f", totalPrice),
                    style = AppTheme.typography.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.active
                    ),
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimension.small),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = String.format("$%.0f", totalPrice),
                    style = AppTheme.typography.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.active
                    ),
                )
            }
        }
    }
}

@Composable
private fun ExpandedMyOrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    onQR: () -> Unit = {},
    onDeleteItem: (itemId: String) -> Unit = {},
) {
    val entity = order.orderEntity
    val totalPrice = order.details.sumOf { it.price * it.quantity }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimension.small))
            .background(AppTheme.color.cardBackground)
    ) {
        order.details.forEachIndexed { index, detail ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimension.small)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = detail.itemPicture,
                        contentDescription = detail.itemName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(AppTheme.dimension.extraSmall)),
                        error = painterResource(R.drawable.placeholder_image),
                        placeholder = painterResource(R.drawable.placeholder_image),
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = detail.itemName,
                                style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${detail.weight} g",
                                style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                            )
                        }

                        entity.expiresAt?.let { expiresAt ->
                            val formatted = formatExpiresAt(expiresAt)
                            Text(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .border(1.dp, AppTheme.color.grey, RoundedCornerShape(50.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                text = "Collect till $formatted",
                                style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entity.establishmentAddress,
                                style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )

                            if (entity.orderStatus == OrderStatus.Reserved) {
                                IconButton(
                                    modifier = Modifier.size(32.dp),
                                    onClick = { onDeleteItem(detail.id) }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.trash),
                                        contentDescription = null,
                                        tint = Color(0xFFFFC107),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (index < order.details.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimension.small)
                        .height(1.dp)
                        .background(AppTheme.color.grey.copy(alpha = 0.2f))
                )
            }
        }

        // Footer with QR and Total Price
        if (entity.orderStatus == OrderStatus.Reserved) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimension.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2D6A4F))
                        .clickable { onQR() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.qr_code),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = String.format("$%.0f", totalPrice),
                    style = AppTheme.typography.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.active
                    ),
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimension.small),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = String.format("$%.0f", totalPrice),
                    style = AppTheme.typography.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.active
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyOrderCardCompactPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(AppTheme.color.background)
                .padding(16.dp)
        ) {
            MyOrderCard(
                order = Order(
                    orderEntity = OrderEntity(
                        id = "1",
                        userId = "u1",
                        totalPrice = 12.0,
                        orderStatus = OrderStatus.Reserved,
                        qrCodeData = "qr",
                        expiresAt = LocalDateTime.now().plusHours(1),
                        establishmentName = "Golden Bakery",
                        establishmentAddress = "Greyson st. 20",
                        establishmentLogo = "",
                        totalOrderWeight = 200,
                        allergens = emptyList(),
                    ),
                    details = listOf(
                        OrderDetailsEntity(
                            id = "d1", orderId = "1", menuPriceId = "m1",
                            quantity = 1, price = 12.0, originalPrice = 12.0,
                            discountPrice = 0.0, itemName = "Pastry Surprise Box",
                            itemType = "pastry", itemPicture = null, weight = 200,
                            minWeight = null, maxWeight = null
                        )
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyOrderCardExpandedPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(AppTheme.color.background)
                .padding(16.dp)
        ) {
            MyOrderCard(
                order = Order(
                    orderEntity = OrderEntity(
                        id = "2",
                        userId = "u1",
                        totalPrice = 95.0,
                        orderStatus = OrderStatus.Reserved,
                        qrCodeData = "qr",
                        expiresAt = LocalDateTime.now().plusHours(2),
                        establishmentName = "Golden Bakery",
                        establishmentAddress = "81 Sumska St, Kharkiv, Ukraine",
                        establishmentLogo = "",
                        totalOrderWeight = 270,
                        allergens = emptyList(),
                    ),
                    details = listOf(
                        OrderDetailsEntity(
                            id = "d1", orderId = "2", menuPriceId = "m1",
                            quantity = 1, price = 45.0, originalPrice = 45.0,
                            discountPrice = 0.0, itemName = "Croissant",
                            itemType = "pastry", itemPicture = null, weight = 120,
                            minWeight = null, maxWeight = null
                        ),
                        OrderDetailsEntity(
                            id = "d2", orderId = "2", menuPriceId = "m2",
                            quantity = 1, price = 50.0, originalPrice = 50.0,
                            discountPrice = 0.0, itemName = "Chocolate Muffin",
                            itemType = "pastry", itemPicture = null, weight = 150,
                            minWeight = null, maxWeight = null
                        )
                    )
                )
            )
        }
    }
}

private fun formatExpiresAt(dateTime: LocalDateTime): String {
    return try {
        dateTime.format(DateTimeFormatter.ofPattern("h a"))
    } catch (e: Exception) {
        ""
    }
}