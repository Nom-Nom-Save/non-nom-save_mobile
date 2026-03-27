package ua.nure.nomnomsave.navigation

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ua.nure.nomnomsave.ui.compose.NNSScreen

fun NavGraphBuilder.listGraph(
    navController: NavController
) {
    navigation<NestedGraph.ListView>(
        startDestination = Screen.List.ListView
    ) {
        composable<Screen.List.ListView> {
            NNSScreen() {
                Text(
                    text = "Screen.List.ListView"
                )
            }
        }

    }
}