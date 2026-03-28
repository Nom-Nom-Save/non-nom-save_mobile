package ua.nure.nomnomsave.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ua.nure.nomnomsave.ui.profile.ProfileScreen

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation<NestedGraph.Profile>(
        startDestination = Screen.Profile
    ) {
        composable<Screen.Profile> {
            ProfileScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
    }
}