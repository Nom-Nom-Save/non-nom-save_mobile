package ua.nure.nomnomsave

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.extention.toPushEvent
import ua.nure.nomnomsave.navigation.NavGraph
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.navigation.topLevelRoutes
import ua.nure.nomnomsave.ui.compose.navBar.NNSBottomNavigationBar
import ua.nure.nomnomsave.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
            }
            else -> {
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            intent?.toPushEvent()?.let {
                PushEventChannel.send(event = it)
                intent = null
            }
        }

        enableEdgeToEdge()
        
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        
        setContent {
            AppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomNavigationBar(navDestination = currentDestination)) {
                            NNSBottomNavigationBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = AppTheme.color.background)
                                    .navigationBarsPadding()
                                    .padding(
                                        start = AppTheme.dimension.small,
                                        end = AppTheme.dimension.small
                                    ),
                                currentDestination = currentDestination,
                                items = topLevelRoutes
                            ) { nestedGraphRoute ->
                                navController.navigate(nestedGraphRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = AppTheme.color.background),
                        contentAlignment = Alignment.Center
                    ) {

                        LaunchedEffect(key1 = Unit) {

                            navController.currentBackStackEntryFlow.first()

                            PushEventChannel.receiveFlow()
                                .distinctUntilChanged()
                                .collect { event ->
                                    navController.navigate(route = event.toScreen()) {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = false
                                        }
                                    }
                                }
                        }

                        NavGraph(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            navController = navController
                        )

                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        lifecycleScope.launch {
            intent.toPushEvent()?.let {
                PushEventChannel.send(event = it)
                setIntent(null)
            }
        }
    }
}

fun showBottomNavigationBar(navDestination: NavDestination?) =
    navDestination?.let { destination ->
        destination.route in listOf(
            Screen.Profile::class.qualifiedName,
            Screen.Cart.CartList::class.qualifiedName,
            Screen.List.ListView::class.qualifiedName,
            Screen.Favorite.FavoritesList::class.qualifiedName,
            Screen.Maps::class.qualifiedName
        )
    } ?: false