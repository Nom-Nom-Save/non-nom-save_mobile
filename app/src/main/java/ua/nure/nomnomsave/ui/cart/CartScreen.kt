package ua.nure.nomnomsave.ui.cart

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.config.dishUrl
import ua.nure.nomnomsave.ui.compose.NNSCartBoxDisplay
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSQRDialog
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.theme.AppTheme
import androidx.core.graphics.createBitmap
import java.time.LocalDateTime

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when(it) {
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
        var search by remember { mutableStateOf(state.query) }
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal),
            label = stringResource(R.string.searchPromt),
            value = search,
        ) {
            search = it
            onAction(Cart.Action.OnQueryChanged(query = it))
        }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .padding(horizontal = AppTheme.dimension.normal)
                    .padding(top = AppTheme.dimension.normal)
            ) {
                items(items = state.orders ?: emptyList(), key = {it.orderEntity.id}) { order ->
                    NNSCartBoxDisplay(
                        title = order.details.firstOrNull()?.itemName ?: "",
                        url = order.orderEntity.establishmentBanner ?: order.orderEntity.establishmentLogo ?: dishUrl,
                        collectTill = order.orderEntity.expiresAt ?: LocalDateTime.now(),
                        allergens = order.orderEntity.allergens,
                        address = order.orderEntity.establishmentAddress,
                        grams = order.orderEntity.totalOrderWeight,
                        onQR = {
                            onAction(Cart.Action.OnQR(data = order.orderEntity.qrCodeData, title = order.details.firstOrNull()?.itemName ?: ""))
                        },
                        onDelete = {
                            onAction(Cart.Action.OnDeleteOrder(id = order.orderEntity.id))
                        }
                    )
                }
            }

        if(state.showQRCodeDialog) {
            NNSQRDialog(
                title = state.qrCodeDialogTitle ?: "",
                bitmap = state.qrBitmap ?: createBitmap(0, 0, Bitmap.Config.RGB_565),
                onDismiss = {
                       onAction(Cart.Action.OnDismissQRCodeDialog(state = false))
                }
            )
        }
    }
}

@Preview
@Composable
fun CartScreenPreview(modifier: Modifier = Modifier) {
    AppTheme {
        CartScreenContent(
            state = Cart.State()
        ) {

        }
    }

}