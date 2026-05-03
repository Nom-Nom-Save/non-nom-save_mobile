package ua.nure.nomnomsave.ui.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.list.ListContract
import ua.nure.nomnomsave.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    pendingMaxDistanceKm: Float?,
    pendingMinRating: Float?,
    pendingTimeFilter: ListContract.TimeFilterOption?,
    pendingCity: String? = null,
    pendingProductTypes: List<String> = emptyList(),
    availableCities: List<String> = emptyList(),
    availableProductTypes: Map<String, String> = emptyMap(),
    hasUserLocation: Boolean = false,
    onDistanceChange: (Float) -> Unit,
    onRatingChange: (Float) -> Unit,
    onTimeFilterChange: (ListContract.TimeFilterOption) -> Unit,
    onCityChange: (String) -> Unit = {},
    onProductTypesChange: (List<String>) -> Unit = {},
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.color.background,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(maxHeight = 500.dp)
                .padding(horizontal = AppTheme.dimension.normal),
            contentPadding = PaddingValues(bottom = AppTheme.dimension.normal),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.normal)
        ) {
            item {
                Text(
                    text = stringResource(R.string.filters),
                    style = AppTheme.typography.large.copy(fontWeight = FontWeight.Bold),
                )
            }

            item {
                //City
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)) {
                    FilterSectionLabel(
                        label = stringResource(R.string.city),
                        valueLabel = null
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
                    ) {
                        if (hasUserLocation) {
                            TimeFilterChip(
                                label = "My location",
                                selected = pendingCity == "My location",
                                onClick = { onCityChange("My location") }
                            )
                        }
                        availableCities.forEach { city ->
                            TimeFilterChip(
                                label = city,
                                selected = pendingCity == city,
                                onClick = { onCityChange(city) }
                            )
                        }
                    }
                }
            }

            item {
                //Distance
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)) {
                    FilterSectionLabel(
                        label = stringResource(R.string.distance),
                        valueLabel = if (pendingMaxDistanceKm != null) {
                            stringResource(R.string.filterUpToKm, pendingMaxDistanceKm.toInt())
                        } else null
                    )
                    if (!hasUserLocation) {
                        Text(
                            text = "Location not available. Please select 'My location' above.",
                            style = AppTheme.typography.small.copy(
                                color = AppTheme.color.grey
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppTheme.dimension.small)
                        )
                    }
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = pendingMaxDistanceKm ?: 1f,
                        onValueChange = onDistanceChange,
                        valueRange = 1f..50f,
                        enabled = hasUserLocation && pendingCity == "My location",
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.color.active,
                            activeTrackColor = AppTheme.color.active,
                            inactiveTrackColor = AppTheme.color.grey.copy(alpha = 0.3f),
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.filter1km),
                            style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
                        )
                        Text(
                            text = stringResource(R.string.filter50km),
                            style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
                        )
                    }
                }
            }

            item {
                //Rating
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)) {
                    FilterSectionLabel(
                        label = stringResource(R.string.rating),
                        valueLabel = null
                    )
                    Slider(
                        modifier = Modifier.fillMaxWidth(),
                        value = pendingMinRating ?: 1f,
                        onValueChange = onRatingChange,
                        valueRange = 1f..5f,
                        colors = SliderDefaults.colors(
                            thumbColor = AppTheme.color.active,
                            activeTrackColor = AppTheme.color.active,
                            inactiveTrackColor = AppTheme.color.grey.copy(alpha = 0.3f),
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.filter1star),
                            style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
                        )
                        Text(
                            text = stringResource(R.string.filter5star),
                            style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
                        )
                    }
                }
            }

            item {
                //Product types
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)) {
                    Text(
                        text = stringResource(R.string.productTypes),
                        style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
                    ) {
                        availableProductTypes.forEach { (id, name) ->
                            TimeFilterChip(
                                label = name,
                                selected = id in pendingProductTypes,
                                onClick = {
                                    val updated = if (id in pendingProductTypes) {
                                        pendingProductTypes - id
                                    } else {
                                        pendingProductTypes + id
                                    }
                                    onProductTypesChange(updated)
                                }
                            )
                        }
                    }
                }
            }

            item {
                NNSButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppTheme.dimension.small),
                    text = stringResource(R.string.saveChanges),
                ) {
                    onApply()
                }
            }
        }
    }
}

@Composable
private fun FilterSectionLabel(label: String, valueLabel: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold)
        )
        if (valueLabel != null) {
            Text(
                text = valueLabel,
                style = AppTheme.typography.small.copy(color = AppTheme.color.active)
            )
        }
    }
}

@Composable
private fun TimeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        color = if (selected) AppTheme.color.active else Color.Transparent,
        border = BorderStroke(1.5.dp, AppTheme.color.active),
        onClick = onClick,
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = AppTheme.dimension.normal,
                vertical = AppTheme.dimension.small
            ),
            text = label,
            style = AppTheme.typography.regular.copy(
                color = if (selected) Color.White else AppTheme.color.active
            ),
        )
    }
}

@Preview
@Composable
private fun FiltersBottomSheetPreview() {
    AppTheme {
        FiltersBottomSheet(
            pendingMaxDistanceKm = 8f,
            pendingMinRating = 3f,
            pendingTimeFilter = ListContract.TimeFilterOption.WITHIN_1_HOUR,
            pendingCity = "Kyiv",
            pendingProductTypes = emptyList(),
            availableCities = listOf("Kyiv", "Lviv", "Odesa"),
            availableProductTypes = mapOf(
                "type1" to "Vegetables",
                "type2" to "Fruits"
            ),
            hasUserLocation = true,
            onDistanceChange = {},
            onRatingChange = {},
            onTimeFilterChange = {},
            onCityChange = {},
            onProductTypesChange = {},
            onApply = {},
            onDismiss = {}
        )
    }
}