package ua.nure.nomnomsave.ui.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.maps.compose.MapsSelectedItem
import ua.nure.nomnomsave.ui.theme.AppTheme
import androidx.core.graphics.createBitmap
import io.ktor.websocket.Frame
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.ui.compose.NNSInputField

@Composable
fun MapsScreen(
    viewModel: MapsViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState(initial = Maps.State())

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                Maps.Event.OnBack -> navController.navigateUp()
                is Maps.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }

    MapsScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreenContent(
    state: Maps.State,
    onAction: (Maps.Action) -> Unit
) {
    NNSScreen {
        val context = LocalContext.current
        val coroutine = rememberCoroutineScope()

        val cameraPositionState = rememberCameraPositionState()
        var hasPermission by remember { mutableStateOf(false) }
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        var mapLoaded by remember { mutableStateOf(false) }
        var searchValue by remember { mutableStateOf("") }

        var suggestions by remember { mutableStateOf(listOf<EstablishmentEntity>()) }
        var suggestionsExpanded by remember { mutableStateOf(false) }
        val suggestionsFiltered by remember(key1 = searchValue) {
            mutableStateOf(
                if(searchValue.isNotEmpty()){
                    suggestions.filter {
                        it.name?.contains(searchValue, ignoreCase = true) ?: false
                    }
                } else {
                    emptyList()
                }
            )
        }

        val mapIconDefault = remember { vectorToBitmapIcon(context, R.drawable.marker_default) }
        val mapIconSelected = remember { vectorToBitmapIcon(context, R.drawable.marker_selected) }

        LaunchedEffect(key1 = searchValue) {
            snapshotFlow {
                searchValue.isNotEmpty()
            }.collect {
                suggestionsExpanded = it
            }
        }

        LaunchedEffect(key1 = state.establishments) {
            suggestions = state.establishments ?: emptyList()
        }

        LaunchedEffect(mapLoaded, hasPermission) {
            if (mapLoaded && hasPermission) {
                getLastKnownLocation(context) { latLng ->
                    coroutine?.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                        )
                    }
                }
            }
        }

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
        }

        if (hasPermission) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .background(color = AppTheme.color.background)
                        .padding(horizontal = AppTheme.dimension.normal)
                        .padding(bottom = AppTheme.dimension.small)
                    ,
                    expanded = suggestionsExpanded,
                    onExpandedChange = {
                        suggestionsExpanded = it
                    }
                ) {
                    NNSInputField(
                        modifier = Modifier
                            .background(color = AppTheme.color.background)
                            .fillMaxWidth()
                        ,
                        label = stringResource(R.string.searchPromt),
                        value = searchValue,
                        onValueChange = {
                            searchValue = it
                        }
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.background(color = AppTheme.color.background),
                        expanded = suggestionsExpanded,
                        onDismissRequest = {
                            suggestionsExpanded = false
                        }
                    ) {
                        suggestionsFiltered.forEach { place ->
                            DropdownMenuItem(
                                modifier = Modifier.background(color = AppTheme.color.background),
                                text = {
                                    Text(
                                        text = place.name ?: "",
                                        style = AppTheme.typography.regular
                                    )
                                },
                                onClick = {
                                    suggestionsExpanded = false
                                    onAction(Maps.Action.OnEstablishmentSelected(establishment = place))
                                    val lat = place.latitude?.toDoubleOrNull()
                                    val long = place.longitude?.toDoubleOrNull()

                                    if(lat != null && long != null) {
                                        coroutine.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), 15f)
                                            )
                                        }
                                    }
                                }
                            )

                        }

                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        properties = MapProperties(
                            isMyLocationEnabled = true
                        ),
                        onMapLoaded = {
                            mapLoaded = true
                        },
                        cameraPositionState = cameraPositionState
                    ) {
                        state.establishments?.forEach { place ->

                            val lat = place.latitude?.toDoubleOrNull()
                            val lng = place.longitude?.toDoubleOrNull()

                            if (lat != null && lng != null) {
                                Marker(
                                    state = MarkerState(
                                        position = LatLng(lat, lng)
                                    ),
                                    title = place.name,
                                    onClick = {
                                        onAction(Maps.Action.OnEstablishmentSelected(establishment = place))
                                        false
                                    },
                                    icon = remember(key1 = state.selectedEstablishment) {
                                        BitmapDescriptorFactory.fromBitmap(if(place == state.selectedEstablishment) mapIconSelected else mapIconDefault)
                                    },
                                )
                            }
                        }
                    }
                    state.selectedEstablishment?.let { place ->
                        MapsSelectedItem(
                            modifier = Modifier
                                .padding(
                                    horizontal = AppTheme.dimension.normal,
                                    vertical = AppTheme.dimension.small
                                ),
                            url = place.banner ?: place.logo ?: "",
                            title = place.name ?: "",
                            comment = place.description ?: "",
                            rating = place.rating ?: "",
                            sale = null,
                            onClick = {
                                onAction(
                                    Maps.Action.OnNavigate(
                                        route = Screen.List.EstablishmentDetails(
                                            id = place.id
                                        )
                                    )
                                )
                            },
                            onClose = {
                                onAction(Maps.Action.OnEstablishmentSelected(establishment = null))
                            }
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
}

private fun vectorToBitmapIcon(context: Context, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    val bitmap = createBitmap(53, 66)
    val canvas = android.graphics.Canvas(bitmap)
    drawable?.setBounds(0, 0, canvas.width, canvas.height)
    drawable?.draw(canvas)
    return bitmap
}

@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getLastKnownLocation(
    context: Context,
    onLocation: (LatLng) -> Unit
) {
    val client = LocationServices.getFusedLocationProviderClient(context)

    client.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocation(
                LatLng(location.latitude, location.longitude)
            )
        }
    }
}

@Preview
@Composable
fun MapsScreenPreviwe(modifier: Modifier = Modifier) {
    AppTheme {
        MapsScreenContent(
            state = Maps.State()
        ) { }
    }

}