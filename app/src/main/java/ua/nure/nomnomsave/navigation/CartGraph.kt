package ua.nure.nomnomsave.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ua.nure.nomnomsave.ui.cart.CartScreen

fun NavGraphBuilder.cartGraph(
    navController: NavController,
    cartViewModel: ua.nure.nomnomsave.ui.cart.CartViewModel
) {
    navigation<NestedGraph.Cart>(
        startDestination = Screen.Cart.CartList
    ) {
        composable<Screen.Cart.CartList> {
            CartScreen(
                viewModel = cartViewModel,
                navController = navController
            )
        }
    }
}