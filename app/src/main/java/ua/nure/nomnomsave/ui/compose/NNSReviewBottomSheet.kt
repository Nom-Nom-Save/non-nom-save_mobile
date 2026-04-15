package ua.nure.nomnomsave.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.ReviewEntity
import ua.nure.nomnomsave.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NNSReviewBottomSheet(
    initialReview: ReviewEntity? = null,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(initialReview?.rating ?: 5) }
    var comment by remember { mutableStateOf(initialReview?.comment ?: "") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppTheme.color.background,
        shape = RoundedCornerShape(topStart = AppTheme.dimension.extralarge, topEnd = AppTheme.dimension.extralarge)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.large)
                .padding(bottom = AppTheme.dimension.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (initialReview == null) stringResource(R.string.writeReview) else stringResource(R.string.editReview),
                style = AppTheme.typography.large
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.large))

            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    Icon(
                        painter = painterResource(id = R.drawable.star_rate),
                        contentDescription = "Star $i",
                        tint = if (i <= rating) AppTheme.color.active else AppTheme.color.grey.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(AppTheme.dimension.iconSize)
                            .clip(RoundedCornerShape(AppTheme.dimension.small))
                            .clickable { rating = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.dimension.large))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.shareThoughts),
                        style = AppTheme.typography.regular.copy(color = AppTheme.color.grey)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.color.active,
                    unfocusedBorderColor = AppTheme.color.grey.copy(alpha = 0.5f),
                    focusedContainerColor = AppTheme.color.background,
                    unfocusedContainerColor = AppTheme.color.background,
                    cursorColor = AppTheme.color.active
                ),
                shape = RoundedCornerShape(AppTheme.dimension.normal),
                textStyle = AppTheme.typography.regular
            )

            Spacer(modifier = Modifier.height(AppTheme.dimension.extralarge))

            NNSButton(
                modifier = Modifier.fillMaxWidth(),
                text = if (initialReview == null) stringResource(R.string.submitReview) else stringResource(R.string.saveChanges),
                onClick = {
                    onSubmit(rating, comment)
                    onDismiss()
                }
            )
        }
    }
}