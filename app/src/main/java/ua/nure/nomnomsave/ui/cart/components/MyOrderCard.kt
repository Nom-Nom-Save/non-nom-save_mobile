package ua.nure.nomnomsave.ui.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    onDelete: () -> Unit = {},
) {
    val entity = order.orderEntity
    val firstItem = order.details.firstOrNull()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimension.small))
            .background(AppTheme.color.background)
            .padding(AppTheme.dimension.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
    ) {
        AsyncImage(
            model = entity.establishmentBanner ?: entity.establishmentLogo,
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
            verticalArrangement = Arrangement.spacedBy(2.dp)
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
                    text = "${entity.totalOrderWeight} g",
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
                    modifier = Modifier.weight(1f),
                    text = entity.establishmentAddress,
                    style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                    maxLines = 1,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = onDelete
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash),
                            contentDescription = null,
                            tint = AppTheme.color.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (entity.orderStatus == OrderStatus.Reserved) {
                        IconButton(
                            modifier = Modifier.size(32.dp),
                            onClick = onQR
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.qr_code),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
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

@Preview(showBackground = true)
@Composable
private fun MyOrderCardPreview() {
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