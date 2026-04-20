package ua.nure.nomnomsave.ui.cart

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.repository.dto.OrderStatus
import ua.nure.nomnomsave.ui.cart.components.MyOrderCard
import ua.nure.nomnomsave.ui.cart.components.OrderCard
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSQRDialog
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                Cart.Event.OnBack -> navController.navigateUp()
                is Cart.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }
    CartScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun CartScreenContent(
    state: Cart.State,
    onAction: (Cart.Action) -> Unit
) {
    NNSScreen {
        CartTabs(
            selectedTab = state.selectedTab,
            onTabSelected = { onAction(Cart.Action.OnTabSelected(it)) }
        )

        when (state.selectedTab) {
            Cart.Tab.ORDER -> OrderTabContent(state = state, onAction = onAction)
            Cart.Tab.MY_ORDERS -> MyOrdersTabContent(state = state, onAction = onAction)
        }

        if (state.showQRCodeDialog) {
            NNSQRDialog(
                title = state.qrCodeDialogTitle ?: "",
                bitmap = state.qrBitmap ?: createBitmap(0, 0, Bitmap.Config.RGB_565),
                onDismiss = { onAction(Cart.Action.OnDismissQRCodeDialog(state = false)) }
            )
        }
    }
}


@Composable
private fun CartTabs(
    selectedTab: Cart.Tab,
    onTabSelected: (Cart.Tab) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Cart.Tab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(
                        vertical = AppTheme.dimension.small
                    ),
                    text = when (tab) {
                        Cart.Tab.ORDER -> stringResource(R.string.tabOrder)
                        Cart.Tab.MY_ORDERS -> stringResource(R.string.tabMyOrders)
                    },
                    style = AppTheme.typography.regular.copy(
                        color = if (isSelected) AppTheme.color.active else AppTheme.color.grey,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    ),
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider(
                    thickness = 2.dp,
                    color = if (isSelected) AppTheme.color.active else Color.Transparent
                )
            }
        }
    }
}


@Composable
private fun OrderTabContent(
    state: Cart.State,
    onAction: (Cart.Action) -> Unit,
) {
    var search by remember { mutableStateOf(state.query) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.searchPromt),
            value = search,
        ) {
            search = it
            onAction(Cart.Action.OnQueryChanged(query = it))
        }

        if (state.localCartItems.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .padding(horizontal = AppTheme.dimension.normal)
                    .padding(top = AppTheme.dimension.normal),
                text = "Your Order (${state.localCartItems.size} items)",
                style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = AppTheme.dimension.normal)
                    .padding(top = AppTheme.dimension.small),
            ) {
                val groupedByEstablishment = state.localCartItems.groupBy { it.establishmentName }
                
                groupedByEstablishment.forEach { (establishmentName, items) ->
                    item(key = establishmentName) {
                        LocalOrderCard(
                            establishmentName = establishmentName,
                            establishmentLogo = items.first().establishmentLogo,
                            establishmentBanner = items.first().establishmentBanner,
                            items = items,
                            onRemoveItem = { menuPriceId ->
                                onAction(Cart.Action.OnRemoveFromLocalCart(menuPriceId))
                            },
                            onOrderItem = { menuPriceId, quantity ->
                                onAction(Cart.Action.OnOrderSingleItem(menuPriceId, quantity))
                            },
                            onOrderAll = { establishment ->
                                onAction(Cart.Action.OnOrderAllFromEstablishment(establishment))
                            }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimension.normal)
                    .padding(top = AppTheme.dimension.normal),
            ) {
                val filtered = (state.orders ?: emptyList()).filter { order ->
                    order.orderStatus == OrderStatus.Reserved
                }.let { list ->
                    if (state.query.isNullOrBlank()) list
                    else list.filter {
                        it.orderEntity.establishmentName.contains(state.query, ignoreCase = true) ||
                                it.details.any { d -> d.itemName.contains(state.query, ignoreCase = true) }
                    }
                }

                items(items = filtered, key = { it.orderEntity.id }) { order ->
                    OrderCard(
                        order = order,
                        onOrder = {
                            onAction(
                                Cart.Action.OnQR(
                                    data = order.orderEntity.qrCodeData,
                                    title = order.details.firstOrNull()?.itemName ?: ""
                                )
                            )
                        },
                        onDelete = { onAction(Cart.Action.OnDeleteOrder(id = order.orderEntity.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyOrdersTabContent(
    state: Cart.State,
    onAction: (Cart.Action) -> Unit,
) {
    val allOrders = state.orders ?: emptyList()

    val upcoming = allOrders.filter { it.orderStatus == OrderStatus.Reserved }
    val completed = allOrders.filter { it.orderStatus == OrderStatus.Completed }
    val cancelledOrExpired = allOrders.filter {
        it.orderStatus == OrderStatus.Cancelled || it.orderStatus == OrderStatus.Expired
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
        ) {
            item {
                state.userStats?.let { stats ->
                    UserStatsCard(
                        successfulOrdersCount = stats.successfulOrdersCount,
                        totalSavings = stats.totalSavings,
                        totalOrderedItems = stats.totalOrderedItems
                    )
                }
            }

            item {
                OrderSectionTitle(title = stringResource(R.string.upcomingOrders))
            }
            if (upcoming.isEmpty()) {
                item { EmptyOrdersText(text = stringResource(R.string.noUpcomingOrders)) }
            } else {
                items(items = upcoming, key = { "upcoming_${it.orderEntity.id}" }) { order ->
                    MyOrderCard(
                        order = order,
                        onQR = {
                            onAction(
                                Cart.Action.OnQR(
                                    data = order.orderEntity.qrCodeData,
                                    title = order.details.firstOrNull()?.itemName ?: ""
                                )
                            )
                        },
                        onDelete = { onAction(Cart.Action.OnDeleteOrder(id = order.orderEntity.id)) }
                    )
                }
            }

            item {
                OrderSectionTitle(
                    modifier = Modifier.padding(top = AppTheme.dimension.small),
                    title = stringResource(R.string.completedOrders)
                )
            }
            if (completed.isEmpty()) {
                item { EmptyOrdersText(text = stringResource(R.string.noCompletedOrders)) }
            } else {
                items(items = completed, key = { "completed_${it.orderEntity.id}" }) { order ->
                    MyOrderCard(order = order)
                }
            }

            item {
                OrderSectionTitle(
                    modifier = Modifier.padding(top = AppTheme.dimension.small),
                    title = stringResource(
                        R.string.cancelledOrExpiredOrders,
                        cancelledOrExpired.size
                    )
                )
            }
            if (cancelledOrExpired.isEmpty()) {
                item { EmptyOrdersText(text = stringResource(R.string.noCancelledOrders)) }
            } else {
                items(items = cancelledOrExpired, key = { "cancelled_${it.orderEntity.id}" }) { order ->
                    MyOrderCard(order = order)
                }
            }
        }
    }
}

@Composable
private fun UserStatsCard(
    successfulOrdersCount: Int,
    totalSavings: Double,
    totalOrderedItems: Int,
) {
    Text(
        text = "Your statistics",
        style = AppTheme.typography.regular.copy(fontWeight = FontWeight.Bold),
        fontSize = AppTheme.typography.regular.fontSize * 1.1f
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(AppTheme.dimension.small)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF4CAF50),
                shape = RoundedCornerShape(AppTheme.dimension.small)
            )
            .padding(AppTheme.dimension.normal)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimension.small),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem(
                label = "Total orders",
                value = successfulOrdersCount.toString(),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "Money saved",
                value = String.format("%.2f ₴", totalSavings),
                modifier = Modifier.weight(1f)
            )
            StatItem(
                label = "Ordered items",
                value = totalOrderedItems.toString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = AppTheme.typography.regular.copy(fontWeight = FontWeight.Bold),
            fontSize = AppTheme.typography.regular.fontSize * 1.2f,
            color = Color(0xFF2E7D32)
        )
        Text(
            text = label,
            style = AppTheme.typography.small,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun OrderSectionTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier,
        text = title,
        style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
    )
}

@Composable
private fun EmptyOrdersText(text: String) {
    Text(
        modifier = Modifier.padding(start = AppTheme.dimension.small),
        text = "• $text",
        style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
    )
}

private val Order.orderStatus get() = this.orderEntity.orderStatus

@Composable
private fun LocalOrderCard(
    establishmentName: String,
    establishmentLogo: String?,
    establishmentBanner: String?,
    items: List<LocalCartItem>,
    onRemoveItem: (String) -> Unit,
    onOrderItem: (String, Int) -> Unit = { _, _ -> },
    onOrderAll: (String) -> Unit = { }
) {
    val firstItem = items.firstOrNull()
    val totalWeight = items.sumOf { it.detail.weight * it.detail.quantity }
    val totalPrice = items.sumOf { it.detail.price * it.detail.quantity }
    val allAllergens = items.flatMap { it.allergens }.distinct()
    val expiresAt = items.firstOrNull()?.expiresAt
    val establishmentAddress = items.firstOrNull()?.establishmentAddress

    Column(
        modifier = Modifier
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
                model = establishmentBanner ?: establishmentLogo,
                contentDescription = establishmentName,
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
                onClick = { }
            ) {
                Icon(
                    painter = painterResource(R.drawable.favorite_passive),
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
                text = establishmentName,
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

        if (!establishmentAddress.isNullOrBlank()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimension.normal)
                    .padding(top = 2.dp),
                text = establishmentAddress,
                style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                maxLines = 1,
            )
        }

        if (firstItem != null) {
            AsyncImage(
                model = firstItem.detail.itemPicture,
                contentDescription = firstItem.detail.itemName,
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
                    text = firstItem.detail.itemName,
                    style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = "$totalWeight g",
                    style = AppTheme.typography.regular.copy(color = AppTheme.color.grey),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            expiresAt?.let {
                OutlinedChip(
                    text = "Collect till ${formatTime(it)}",
                    borderColor = AppTheme.color.grey,
                    textColor = AppTheme.color.grey,
                )
            }

            if (allAllergens.isNotEmpty()) {
                OutlinedChip(
                    text = "Allergens",
                    borderColor = AppTheme.color.error,
                    textColor = AppTheme.color.error,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppTheme.dimension.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.detail.itemName,
                            style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
                        )
                        Text(
                            text = "Qty: ${item.detail.quantity} × ${String.format("%.2f", item.detail.price)} ₴",
                            style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                        )
                    }
                    
                    Text(
                        text = String.format("%.2f", item.detail.price * item.detail.quantity) + " ₴",
                        style = AppTheme.typography.small.copy(fontWeight = FontWeight.SemiBold),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = AppTheme.dimension.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = { onRemoveItem(item.detail.menuPriceId) }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash),
                            contentDescription = null,
                            tint = AppTheme.color.error,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    NNSButton(
                        modifier = Modifier.weight(1f),
                        text = "Order",
                        onClick = { 
                            Log.d("CartScreen", "Order button clicked for: ${item.detail.itemName}, menuPriceId: ${item.detail.menuPriceId}, quantity: ${item.detail.quantity}")
                            onOrderItem(item.detail.menuPriceId, item.detail.quantity) 
                        }
                    )
                }
                
                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = AppTheme.dimension.small),
                        color = AppTheme.color.background
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Total: ${String.format("%.2f", totalPrice)} ₴",
                style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold, color = AppTheme.color.active),
            )

            NNSButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Order All from ${establishmentName}",
                onClick = { onOrderAll(establishmentName) }
            )
        }

        Box(modifier = Modifier.height(AppTheme.dimension.normal))
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

private fun formatTime(dateTime: java.time.LocalDateTime): String {
    return try {
        dateTime.format(java.time.format.DateTimeFormatter.ofPattern("h a"))
    } catch (e: Exception) {
        ""
    }
}


@Preview(showSystemUi = true)
@Composable
fun CartScreenPreview() {
    AppTheme {
        CartScreenContent(state = Cart.State()) {}
    }
}

@Preview(showSystemUi = true)
@Composable
fun CartScreenMyOrdersPreview() {
    AppTheme {
        CartScreenContent(state = Cart.State(selectedTab = Cart.Tab.MY_ORDERS)) {}
    }
}