package ua.nure.nomnomsave.ui.cart

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.repository.dto.OrderStatus
import ua.nure.nomnomsave.ui.cart.components.MyOrderCard
import ua.nure.nomnomsave.ui.cart.components.OrderCard
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