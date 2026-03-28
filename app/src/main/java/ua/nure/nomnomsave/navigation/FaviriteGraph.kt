package ua.nure.nomnomsave.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ua.nure.nomnomsave.ui.favorite.FavoritesScreen

fun NavGraphBuilder.favoriteGraph(navController: NavController) {
    navigation<NestedGraph.Favorite>(
        startDestination = Screen.Favorite.FavoritesList
    ) {
        composable<Screen.Favorite.FavoritesList> {
            FavoritesScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
    }
}