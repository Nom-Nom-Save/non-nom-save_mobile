package ua.nure.nomnomsave.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ua.nure.nomnomsave.ui.auth.forgotpassword.ForgotPasswordScreen
import ua.nure.nomnomsave.ui.auth.login.LoginScreen
import ua.nure.nomnomsave.ui.auth.register.RegistrationScreen
import ua.nure.nomnomsave.ui.establishmentDetails.EstablishmentDetailsScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val cartViewModel: ua.nure.nomnomsave.ui.cart.CartViewModel = hiltViewModel()
    
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Auth.SignIn
    ) {
        composable<Screen.Auth.Registration> {
            RegistrationScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable<Screen.Auth.SignIn> {
            LoginScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable<Screen.Auth.ForgotPassword> {
            ForgotPasswordScreen(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }
        composable<Screen.List.EstablishmentDetails> {
            EstablishmentDetailsScreen(
                viewModel = hiltViewModel(),
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        cartGraph(navController = navController, cartViewModel = cartViewModel)
        listGraph(navController = navController)
        favoriteGraph(navController = navController)
        profileGraph(navController = navController)
        mapGraph(navController = navController)
    }

}