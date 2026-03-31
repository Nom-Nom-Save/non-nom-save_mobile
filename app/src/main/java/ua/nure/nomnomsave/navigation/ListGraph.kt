package ua.nure.nomnomsave.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ua.nure.nomnomsave.ui.list.ListScreen
import ua.nure.nomnomsave.ui.list.ListViewModel

fun NavGraphBuilder.listGraph(
    navController: NavController
) {
    navigation<NestedGraph.ListView>(
        startDestination = Screen.List.ListView
    ) {
        composable<Screen.List.ListView> {
            ListScreen(
                viewModel = hiltViewModel<ListViewModel>(),
                navController = navController
            )
        }
    }
}