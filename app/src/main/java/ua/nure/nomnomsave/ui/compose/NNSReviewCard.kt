package ua.nure.nomnomsave.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.Locale
private fun formatIsoDate(isoDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val parsedDate = parser.parse(isoDate)

        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        parsedDate?.let { formatter.format(it) } ?: isoDate.take(10)
    } catch (e: Exception) {
        isoDate.take(10)
    }
}

@Composable
fun NNSReviewCard(
    modifier: Modifier = Modifier,
    name: String,
    date: String,
    rating: Int,
    text: String,
    isMyReview: Boolean = false,
    isEditable: Boolean = false,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimension.large))
            .background(AppTheme.color.cardBackground)
            .padding(AppTheme.dimension.normal)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // Рядок з іменем ТА датою
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = AppTheme.typography.regular.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.width(AppTheme.dimension.small))
                    Text(
                        text = formatIsoDate(date),
                        style = AppTheme.typography.regular.copy(
                            color = AppTheme.color.grey,
                            fontSize = 12.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(AppTheme.dimension.extraSmall))

                Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.extraSmall)) {
                    repeat(rating) {
                        Icon(
                            painter = painterResource(R.drawable.star_rate),
                            contentDescription = null,
                            tint = AppTheme.color.active,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (isMyReview) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isEditable) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(AppTheme.dimension.extralarge)) {
                            Icon(
                                modifier = Modifier.size(AppTheme.dimension.large),
                                painter = painterResource(id = R.drawable.edit_icon),
                                contentDescription = "Edit Review",
                                tint = AppTheme.color.grey
                            )
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(AppTheme.dimension.extralarge)) {
                        Icon(
                            modifier = Modifier.size(AppTheme.dimension.large),
                            painter = painterResource(id = R.drawable.trash),
                            contentDescription = "Delete Review",
                            tint = AppTheme.color.error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.dimension.normal))

        Text(
            text = text,
            style = AppTheme.typography.regular
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NNSReviewCardPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NNSReviewCard(
                name = "Alice",
                date = "2026-04-13T17:48:55.838Z",
                rating = 5,
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                isMyReview = true,
                isEditable = true
            )
        }
    }
}