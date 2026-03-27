package ua.nure.nomnomsave.ui.cart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.room.util.query
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.theme.AppTheme

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