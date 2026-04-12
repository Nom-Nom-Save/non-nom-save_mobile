package ua.nure.nomnomsave.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ua.nure.nomnomsave.ui.maps.MapsScreen

fun NavGraphBuilder.mapGraph(navController: NavController) {
    navigation<NestedGraph.Maps> (
        startDestination = Screen.Maps
    ) {
        composable<Screen.Maps> {
            MapsScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
    }
}