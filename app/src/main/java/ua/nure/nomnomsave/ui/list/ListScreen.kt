package ua.nure.nomnomsave.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.list.components.EstablishmentCard
import ua.nure.nomnomsave.ui.list.components.FiltersBottomSheet
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun ListScreen(
    viewModel: ListViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is ListContract.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }

    ListScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ListScreenContent(
    state: ListContract.State,
    onAction: (ListContract.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
    ) {
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.searchPromt),
            value = state.searchQuery,
        ) {
            onAction(ListContract.Action.OnSearchChange(query = it))
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimension.small),
            contentPadding = PaddingValues(horizontal = AppTheme.dimension.normal),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
        ) {
            item {
                SortChip(
                    label = stringResource(R.string.distance),
                    sortDirection = if (state.selectedSort == ListContract.SortOption.DISTANCE) state.sortDirection else ListContract.SortDirection.ASCENDING,
                    onClick = {
                        if (state.selectedSort == ListContract.SortOption.DISTANCE) {
                            onAction(ListContract.Action.OnSortDirectionChange(state.sortDirection.toggle()))
                        } else {
                            onAction(ListContract.Action.OnSortChange(ListContract.SortOption.DISTANCE))
                        }
                    }
                )
            }
            item {
                SortChip(
                    label = stringResource(R.string.rating),
                    sortDirection = if (state.selectedSort == ListContract.SortOption.RATING) state.sortDirection else ListContract.SortDirection.ASCENDING,
                    onClick = {
                        if (state.selectedSort == ListContract.SortOption.RATING) {
                            onAction(ListContract.Action.OnSortDirectionChange(state.sortDirection.toggle()))
                        } else {
                            onAction(ListContract.Action.OnSortChange(ListContract.SortOption.RATING))
                        }
                    }
                )
            }
            item {
                SortChip(
                    label = "Closing Time",
                    sortDirection = if (state.selectedSort == ListContract.SortOption.CLOSING_TIME) state.sortDirection else ListContract.SortDirection.ASCENDING,
                    onClick = {
                        if (state.selectedSort == ListContract.SortOption.CLOSING_TIME) {
                            onAction(ListContract.Action.OnSortDirectionChange(state.sortDirection.toggle()))
                        } else {
                            onAction(ListContract.Action.OnSortChange(ListContract.SortOption.CLOSING_TIME))
                        }
                    }
                )
            }
            item {
                FilterChip(
                    label = stringResource(R.string.filters),
                    onClick = { onAction(ListContract.Action.OnShowFilters) }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (state.isLoading && state.filteredEstablishments.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppTheme.color.active
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = AppTheme.dimension.normal,
                        vertical = AppTheme.dimension.small
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
                ) {
                    items(
                        items = state.filteredEstablishments,
                        key = { it.id }
                    ) { entity ->
                        EstablishmentCard(
                            entity = entity,
                            isFavorite = state.favorites?.any {it.establishment.id == entity.id} ?: false,
                            onFavoriteClick = {
                                onAction(ListContract.Action.OnFavoriteToggle(id = entity.id))
                            },
                            onClick = {
                                 onAction(ListContract.Action.OnNavigate(Screen.List.EstablishmentDetails(entity.id)))
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.showFilters) {
        FiltersBottomSheet(
            pendingMaxDistanceKm = state.pendingMaxDistanceKm,
            pendingMinRating = state.pendingMinRating,
            pendingTimeFilter = state.pendingTimeFilter,
            pendingCity = state.pendingCity,
            pendingProductTypes = state.pendingProductTypes,
            availableCities = state.availableCities,
            availableProductTypes = state.availableProductTypes,
            hasUserLocation = state.userLat != null && state.userLon != null,
            onDistanceChange = { onAction(ListContract.Action.OnDistanceChange(km = it)) },
            onRatingChange = { onAction(ListContract.Action.OnRatingChange(rating = it)) },
            onTimeFilterChange = { option: ListContract.TimeFilterOption -> onAction(ListContract.Action.OnTimeFilterChange(option = option)) },
            onCityChange = { onAction(ListContract.Action.OnCityChange(city = it)) },
            onProductTypesChange = { onAction(ListContract.Action.OnProductTypesChange(typeIds = it)) },
            onApply = { onAction(ListContract.Action.OnApplyFilters) },
            onDismiss = { onAction(ListContract.Action.OnDismissFilters) }
        )
    }
}

@Composable
private fun SortChip(
    label: String,
    sortDirection: ListContract.SortDirection,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(AppTheme.color.active)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = AppTheme.typography.small.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )
        Icon(
            painter = painterResource(
                if (sortDirection == ListContract.SortDirection.ASCENDING) 
                    R.drawable.arrow_up else R.drawable.arrow_down
            ),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(AppTheme.color.active)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = AppTheme.typography.small.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ListScreenPreview() {
    AppTheme {
        ListScreenContent(
            state = ListContract.State(
                filteredEstablishments = listOf(
                    EstablishmentEntity(id = "1", name = "The golden bakery", workingHours = "closes in 1 hour", adress = "Street", rating = "5.0"),
                    EstablishmentEntity(id = "2", name = "The golden bakery", workingHours = "closes in 1 hour", adress = "Street", rating = "4.5"),
                    EstablishmentEntity(id = "3", name = "The golden bakery", workingHours = "closes in 1 hour", adress = "Street", rating = "3.0"),
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ListScreenDarkPreview() {
    AppTheme {
        ListScreenContent(
            state = ListContract.State(
                filteredEstablishments = listOf(
                    EstablishmentEntity(id = "1", name = "The golden bakery", workingHours = "closes in 1 hour", adress = "Street", rating = "5.0"),
                )
            ),
            onAction = {}
        )
    }
}