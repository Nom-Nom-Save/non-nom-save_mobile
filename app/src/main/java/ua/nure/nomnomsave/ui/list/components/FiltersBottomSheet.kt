package ua.nure.nomnomsave.ui.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ua.nure.nomnomsave.ui.list.List
import ua.nure.nomnomsave.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersBottomSheet(
    modifier: Modifier = Modifier,
    pendingMaxDistanceKm: Float,
    pendingMinRating: Float,
    pendingTimeFilter: List.TimeFilterOption?,
    onDistanceChange: (Float) -> Unit,
    onRatingChange: (Float) -> Unit,
    onTimeFilterChange: (List.TimeFilterOption) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppTheme.color.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(bottom = AppTheme.dimension.normal),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.normal)
        ) {
            Text(
                text = stringResource(R.string.filters),
                style = AppTheme.typography.large.copy(fontWeight = FontWeight.Bold),
            )

            FilterSectionLabel(
                label = stringResource(R.string.distance),
                valueLabel = stringResource(R.string.filterUpToKm, pendingMaxDistanceKm.toInt())
            )
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = pendingMaxDistanceKm,
                onValueChange = onDistanceChange,
                valueRange = 1f..10f,
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
                    text = stringResource(R.string.filter10km),
                    style = AppTheme.typography.small.copy(color = AppTheme.color.grey)
                )
            }

            FilterSectionLabel(
                label = stringResource(R.string.rating),
                valueLabel = null
            )
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = pendingMinRating,
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

            Text(
                text = stringResource(R.string.filterTimeUntilClosing),
                style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
            ) {
                List.TimeFilterOption.entries.forEach { option ->
                    TimeFilterChip(
                        label = option.label,
                        selected = pendingTimeFilter == option,
                        onClick = { onTimeFilterChange(option) }
                    )
                }
            }

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
            pendingTimeFilter = List.TimeFilterOption.WITHIN_1_HOUR,
            onDistanceChange = {},
            onRatingChange = {},
            onTimeFilterChange = {},
            onApply = {},
            onDismiss = {}
        )
    }
}