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
                is List.Event.OnNavigate -> navController.navigate(route = it.route)
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
    state: List.State,
    onAction: (List.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
    ) {
        // Search bar — NNSInputField з проекту
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal),
            label = stringResource(R.string.searchPromt),
            value = state.searchQuery,
        ) {
            onAction(List.Action.OnSearchChange(query = it))
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
                    label = stringResource(R.string.price),
                    ascending = state.selectedSort != List.SortOption.PRICE_DESC,
                    onClick = {
                        val next = if (state.selectedSort == List.SortOption.PRICE_ASC)
                            List.SortOption.PRICE_DESC else List.SortOption.PRICE_ASC
                        onAction(List.Action.OnSortChange(next))
                    }
                )
            }
            item {
                SortChip(
                    label = stringResource(R.string.distance),
                    ascending = state.selectedSort != List.SortOption.DISTANCE_DESC,
                    onClick = {
                        val next = if (state.selectedSort == List.SortOption.DISTANCE_ASC)
                            List.SortOption.DISTANCE_DESC else List.SortOption.DISTANCE_ASC
                        onAction(List.Action.OnSortChange(next))
                    }
                )
            }
            item {
                SortChip(
                    label = stringResource(R.string.rating),
                    ascending = state.selectedSort != List.SortOption.RATING_DESC,
                    onClick = {
                        val next = if (state.selectedSort == List.SortOption.RATING_ASC)
                            List.SortOption.RATING_DESC else List.SortOption.RATING_ASC
                        onAction(List.Action.OnSortChange(next))
                    }
                )
            }
            item {
                // "Filters" chip — без стрілки, outlined стиль
                FilterChip(
                    label = stringResource(R.string.filters),
                    onClick = { onAction(List.Action.OnShowFilters) }
                )
            }
        }

        // List content
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
                            isFavorite = state.favorites.contains(entity.id),
                            onFavoriteClick = {
                                onAction(List.Action.OnFavoriteToggle(id = entity.id))
                            },
                            onClick = {
                                // onAction(List.Action.OnNavigate(Screen.Establishment(entity.id)))
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
            onDistanceChange = { onAction(List.Action.OnDistanceChange(km = it)) },
            onRatingChange = { onAction(List.Action.OnRatingChange(rating = it)) },
            onTimeFilterChange = { onAction(List.Action.OnTimeFilterChange(option = it)) },
            onApply = { onAction(List.Action.OnApplyFilters) },
            onDismiss = { onAction(List.Action.OnDismissFilters) }
        )
    }
}

@Composable
private fun SortChip(
    label: String,
    ascending: Boolean,
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
            painter = painterResource(if (ascending) R.drawable.arrow_up else R.drawable.arrow_down),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
    }
}

// Chip без фону, лише border — для Filters
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
            state = List.State(
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
            state = List.State(
                filteredEstablishments = listOf(
                    EstablishmentEntity(id = "1", name = "The golden bakery", workingHours = "closes in 1 hour", adress = "Street", rating = "5.0"),
                )
            ),
            onAction = {}
        )
    }
}