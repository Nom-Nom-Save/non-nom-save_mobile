package ua.nure.nomnomsave.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ua.nure.nomnomsave.ui.auth.register.RegistrationScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
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
            Text("Sign in screen")
            Button(
                onClick = {
                    navController.navigate(route = Screen.Auth.Registration)
                }
            ) {
                Text("to registration")
            }
//            SignInScreen(
//                viewModel = hiltViewModel(),
//                navController = navController
//            )
        }
        composable<Screen.Auth.ForgotPassword> {
//            ForgotPasswordScreen(
//                viewModel = hiltViewModel(),
//                navController = navController
//            )
        }

    }

}