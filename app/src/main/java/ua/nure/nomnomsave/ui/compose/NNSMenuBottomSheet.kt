package ua.nure.nomnomsave.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.ItemDetailsEntity
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.db.data.entity.PriceDataEntity
import ua.nure.nomnomsave.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NNSMenuBottomSheet(
    menuItem: MenuEntity,
    onDismiss: () -> Unit,
    onReserve: (Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    val maxQuantity = (menuItem.priceData?.availableQuantity ?: 10).coerceAtLeast(1)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppTheme.color.background,
        shape = RoundedCornerShape(topStart = AppTheme.dimension.extralarge, topEnd = AppTheme.dimension.extralarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.large)
                .padding(bottom = AppTheme.dimension.large)
        ) {

            Text(
                text = menuItem.itemDetails?.name ?: "Pastry Surprise Box",
                style = AppTheme.typography.large.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp)
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.small))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$${menuItem.priceData?.discountPrice}",
                    style = AppTheme.typography.large.copy(
                        color = AppTheme.color.active,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )
                Spacer(modifier = Modifier.width(AppTheme.dimension.small))
                if (menuItem.priceData?.originalPrice != null) {
                    Text(
                        text = "$${menuItem.priceData.originalPrice}",
                        style = AppTheme.typography.regular.copy(
                            color = AppTheme.color.grey,
                            textDecoration = TextDecoration.LineThrough
                        ),
                        modifier = Modifier.padding(bottom = AppTheme.dimension.extraSmall)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.dimension.normal))

            Text(
                text = String.format(stringResource(R.string.itemsLeft), menuItem.priceData?.availableQuantity ?: 0),
                style = AppTheme.typography.regular.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.large))

            Text(
                text = stringResource(R.string.pickUpTime),
                style = AppTheme.typography.large.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = String.format(stringResource(R.string.collectTil), menuItem.priceData?.endTime ?: "N/A"),
                style = AppTheme.typography.regular
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.large))

            Text(
                text = stringResource(R.string.types),
                style = AppTheme.typography.large.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = menuItem.itemDetails?.types?.joinToString(", ") ?: "",
                style = AppTheme.typography.regular
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.large))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quantity",
                    style = AppTheme.typography.large
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, AppTheme.color.grey.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Кнопка МИНУС
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { if (quantity > 1) quantity-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "-",
                            tint = AppTheme.color.grey,
                            modifier = Modifier.size(AppTheme.dimension.iconSize)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(AppTheme.color.grey.copy(alpha = 0.2f))
                            .height(36.dp)
                            .padding(horizontal = AppTheme.dimension.normal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = quantity.toString(), style = AppTheme.typography.large)
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { if (quantity < maxQuantity) quantity++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "+",
                            tint = AppTheme.color.grey,
                            modifier = Modifier.size(AppTheme.dimension.iconSize)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.dimension.extralarge))

            NNSButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Reserve Now",
                onClick = {
                    onReserve(quantity)
                    onDismiss()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NNSMenuBottomSheetPreview() {
    AppTheme {
        NNSMenuBottomSheet(
            menuItem = MenuEntity(
                id = "dummy_1",
                itemDetails = ItemDetailsEntity(
                    name = "Pastry Surprise Box",
                    types = listOf("Bread", "Tarts", "Muffins")
                ),
                priceData = PriceDataEntity(
                    discountPrice = 4.50,
                    originalPrice = 15.00,
                    availableQuantity = 5,
                    endTime = "19:00"
                )
            ),
            onDismiss = {},
            onReserve = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NNSMenuBottomSheetDarkPreview() {
    AppTheme {
        NNSMenuBottomSheet(
            menuItem = MenuEntity(
                id = "dummy_1",
                itemDetails = ItemDetailsEntity(
                    name = "Pastry Surprise Box",
                    types = listOf("Bread", "Tarts", "Muffins")
                ),
                priceData = PriceDataEntity(
                    discountPrice = 4.50,
                    originalPrice = 15.00,
                    availableQuantity = 5,
                    endTime = "19:00"
                )
            ),
            onDismiss = {},
            onReserve = {}
        )
    }
}