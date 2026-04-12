package ua.nure.nomnomsave.ui.maps

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun MapsScreen(
    viewModel: MapsViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState(initial = Maps.State())
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }
    val permission = android.Manifest.permission.ACCESS_FINE_LOCATION


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            hasPermission = true
        } else {
            launcher.launch(permission)
        }

        viewModel.event.collect {
            when (it) {
                Maps.Event.OnBack -> navController.navigateUp()
                is Maps.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }

    if (hasPermission) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(
                isMyLocationEnabled = true
            )
        ) {
            state.establishments?.forEach { place ->

                val lat = place.latitude?.toDoubleOrNull()
                val lng = place.longitude?.toDoubleOrNull()

                if (lat != null && lng != null) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(lat, lng)
                        ),
                        title = place.name ?: "No name",
                        snippet = place.adress ?: ""
                    )
                }
            }
        }

    } else {
        Text(
            style = AppTheme.typography.regular,
            text = "Location permission is required"
        )
    }
}