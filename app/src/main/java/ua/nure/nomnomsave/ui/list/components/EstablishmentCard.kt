package ua.nure.nomnomsave.ui.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun EstablishmentCard(
    modifier: Modifier = Modifier,
    entity: EstablishmentEntity,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.dimension.small))
            .background(AppTheme.color.cardBackground)
            .clickable { onClick() }
            .padding(AppTheme.dimension.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimension.small)
    ) {
        if (LocalInspectionMode.current) {
            AsyncImage(
                model = R.drawable.placeholder_image,
                contentDescription = entity.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(AppTheme.dimension.extraSmall)),
            )
        } else {
            AsyncImage(
                model = entity.logo ?: entity.banner,
                contentDescription = entity.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(AppTheme.dimension.extraSmall)),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = entity.name.orEmpty(),
                    style = AppTheme.typography.regular.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                )
                RatingBadge(rating = entity.rating?.toFloatOrNull())
            }

            Text(
                text = entity.workingHours?.let { hours ->
                    try {
                        val firstDay = hours.split("|").firstOrNull() ?: "-"
                        val parts = firstDay.split("=")
                        if (parts.size == 2) {
                            val time = parts[1].trim()
                            time
                        } else {
                            firstDay
                        }
                    } catch (e: Exception) {
                        hours
                    }
                } ?: "-",
                style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                maxLines = 1,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = entity.adress.orEmpty(),
                    style = AppTheme.typography.small.copy(color = AppTheme.color.grey),
                    maxLines = 1,
                )
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = onFavoriteClick
                ) {
                    Icon(
                        painter = painterResource(
                            if (isFavorite) R.drawable.heart else R.drawable.favorite_passive
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingBadge(rating: Float?) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(AppTheme.color.active)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = rating?.let { "%.1f".format(it) } ?: "—",
            style = AppTheme.typography.small.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        )
        Icon(
            painter = painterResource(R.drawable.star_rate),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EstablishmentCardPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .background(AppTheme.color.background)
                .padding(16.dp)
        ) {
            EstablishmentCard(
                entity = EstablishmentEntity(
                    id = "1",
                    name = "The golden bakery",
                    workingHours = "mon=09:00-18:00|tue=09:00-18:00|wed=09:00-18:00|thu=09:00-18:00|fri=09:00-18:00|sat=10:00-16:00|sun=closed",
                    adress = "Street",
                    rating = "5.0",
                ),
                isFavorite = true,
            )
        }
    }
}