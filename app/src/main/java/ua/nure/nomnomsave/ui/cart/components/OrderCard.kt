package ua.nure.nomnomsave.ui.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.theme.AppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    order: Order,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onOrder: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val entity = order.orderEntity
    val firstItem = order.details.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimension.normal))
            .background(AppTheme.color.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AsyncImage(
                model = entity.establishmentBanner ?: entity.establishmentLogo,
                contentDescription = entity.establishmentName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(
                        topStart = AppTheme.dimension.normal,
                        topEnd = AppTheme.dimension.normal
                    )),
                placeholder = painterResource(R.drawable.placeholder_image),
                error = painterResource(R.drawable.placeholder_image),
            )

            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(AppTheme.dimension.small)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.background),
                onClick = onFavoriteClick
            ) {
                Icon(
                    painter = painterResource(
                        if (isFavorite) R.drawable.favorite_active else R.drawable.favorite_passive
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = entity.establishmentName,
                style = AppTheme.typography.large.copy(fontWeight = FontWeight.SemiBold),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "5",
                    style = AppTheme.typography.regular.copy(
                        color = AppTheme.color.active,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Icon(
                    painter = painterResource(R.drawable.star_rate),
                    contentDescription = null,
                    tint = AppTheme.color.active,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = 2.dp),
            text = entity.establishmentAddress,
            style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
            maxLines = 1,
        )

        AsyncImage(
            model = firstItem?.itemPicture,
            contentDescription = firstItem?.itemName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.small)
                .clip(RoundedCornerShape(AppTheme.dimension.small)),
            placeholder = painterResource(R.drawable.placeholder_image),
            error = painterResource(R.drawable.placeholder_image),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = firstItem?.itemName.orEmpty(),
                style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "${entity.totalOrderWeight} g",
                style = AppTheme.typography.regular.copy(color = AppTheme.color.grey),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            entity.expiresAt?.let {
                OutlinedChip(
                    text = "Collect till ${formatTime(it)}",
                    borderColor = AppTheme.color.grey,
                    textColor = AppTheme.color.grey,
                )
            }

            if (entity.allergens.isNotEmpty()) {
                OutlinedChip(
                    text = "Allergens",
                    borderColor = AppTheme.color.error,
                    textColor = AppTheme.color.error,
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = 2.dp),
            text = entity.establishmentAddress,
            style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
            maxLines = 1,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimension.normal),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.size(36.dp),
                onClick = onDelete
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = null,
                    tint = AppTheme.color.error,
                    modifier = Modifier.size(22.dp)
                )
            }

            NNSButton(
                modifier = Modifier,
                text = "Order",
            ) {
                onOrder()
            }
        }
    }
}

@Composable
private fun OutlinedChip(
    text: String,
    borderColor: Color,
    textColor: Color,
) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        text = text,
        style = AppTheme.typography.small.copy(color = textColor),
    )
}

private fun formatTime(dateTime: LocalDateTime): String {
    return try {
        dateTime.format(DateTimeFormatter.ofPattern("h a"))
    } catch (e: Exception) {
        ""
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderCardPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp)
        ) {
            OrderCard(
                order = Order(
                    orderEntity = OrderEntity(
                        id = "1",
                        userId = "u1",
                        totalPrice = 12.0,
                        orderStatus = OrderStatus.Reserved,
                        qrCodeData = "qr",
                        expiresAt = LocalDateTime.now().plusHours(1),
                        establishmentName = "Golden bakery",
                        establishmentAddress = "Artisan Bakery",
                        establishmentLogo = "",
                        totalOrderWeight = 200,
                        allergens = listOf("Gluten", "Nuts"),
                    ),
                    details = listOf(
                        OrderDetailsEntity(
                            id = "d1",
                            orderId = "1",
                            menuPriceId = "m1",
                            quantity = 1,
                            price = 12.0,
                            originalPrice = 12.0,
                            discountPrice = 0.0,
                            itemName = "Pastry Surprise Box",
                            itemType = "pastry",
                            weight = 200,
                        )
                    )
                ),
                isFavorite = true,
            )
        }
    }
}