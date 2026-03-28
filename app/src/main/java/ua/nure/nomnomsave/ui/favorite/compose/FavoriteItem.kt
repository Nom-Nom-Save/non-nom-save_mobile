package ua.nure.nomnomsave.ui.favorite.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun FavoriteItem(
    modifier: Modifier,
    url: String,
    title: String,
    comment: String,
    rating: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(40.dp))
            .background(color = AppTheme.color.cardBackground, shape = RoundedCornerShape(40.dp)),
        ) {
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            if (LocalInspectionMode.current) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(181.dp)
                        .clip(shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                    model = R.drawable.logo_light,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(181.dp)
                        .clip(shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                    model = url,
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
            Icon(
                modifier = Modifier
                    .padding(end = AppTheme.dimension.normal, top = AppTheme.dimension.normal)
                    .size(96.dp)
                    .clip(shape = CircleShape)
                    .clickable(onClick = onClick)
                    .border(width = 1.dp, color = AppTheme.color.accent, shape = CircleShape)
                    .background(color = Color.White, shape = CircleShape)
                    .padding(
                        top = 22.dp,
                        bottom = AppTheme.dimension.normal,
                        start = AppTheme.dimension.normal,
                        end = AppTheme.dimension.normal
                    )
                ,
                painter = painterResource(R.drawable.heart),
                contentDescription = null,
                tint = AppTheme.color.accent
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = AppTheme.dimension.normal,
                    horizontal = AppTheme.dimension.normal
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = AppTheme.typography.large
            )
            Text(
                text = rating,
                style = AppTheme.typography.large.copy(
                    color = AppTheme.color.ratingColor
                )
            )
        }
        Text(
            modifier = Modifier
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(bottom = AppTheme.dimension.extralarge),
            text = comment,
            style = AppTheme.typography.small
        )
    }
}

@Preview
@Composable
private fun FavoriteItemPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = Modifier.background(color = AppTheme.color.background)) {
            FavoriteItem(
                modifier = Modifier,
                url = "",
                title = "Golden bakery",
                comment = "Artisan Bakery",
                rating = "5"
            ) {}
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FavoriteItemDarkPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = Modifier.background(color = AppTheme.color.background)) {
            FavoriteItem(
                modifier = Modifier,
                url = "",
                title = "Golden bakery",
                comment = "Artisan Bakery",
                rating = "5"
            ) {}
        }
    }
}