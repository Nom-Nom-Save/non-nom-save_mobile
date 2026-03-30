package ua.nure.nomnomsave.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ua.nure.nomnomsave.App
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun NNSMenuCard(
    modifier: Modifier = Modifier,
    title: String,
    url: String,
    collectTill: String,
    allergens: Boolean = false,
    grams: Int,
    picture: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(40.dp))
            .background(color = AppTheme.color.background, shape = RoundedCornerShape(40.dp)),

        ) {
        if(LocalInspectionMode.current) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(181.dp)
                    .clip(shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                model = picture,
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
                text = String.format(stringResource(R.string.grams), grams),
                style = AppTheme.typography.large
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                modifier = Modifier
                    .clip(AppTheme.shape.accentShape)
                    .border(width = 1.dp, color = AppTheme.color.grey, shape = AppTheme.shape.accentShape)
                    .padding(
                        horizontal = AppTheme.dimension.small,
                        vertical = 2.dp
                    )
                ,
                text = String.format(stringResource(R.string.collectTil), collectTill),
                style = AppTheme.typography.regular.copy(
                    color = AppTheme.color.grey
                )
            )
            if(allergens) {
                Text(
                    modifier = Modifier
                        .padding(start = AppTheme.dimension.extraSmall)
                        .clip(AppTheme.shape.accentShape)
                        .border(width = 1.dp, color = AppTheme.color.grey, shape = AppTheme.shape.accentShape)
                        .padding(
                            horizontal = AppTheme.dimension.small,
                            vertical = 2.dp
                        )
                    ,
                    text = stringResource(R.string.allergens),
                    style = AppTheme.typography.regular.copy(
                        color = AppTheme.color.error
                    )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(
                    vertical = AppTheme.dimension.normal,
                    horizontal = AppTheme.dimension.normal
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = AppTheme.dimension.normal,
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        // .clickable(onClick = onQR)
                        .background(color = AppTheme.color.active)
                        .padding(
                            horizontal = AppTheme.dimension.normal,
                            vertical = AppTheme.dimension.normal
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = stringResource(R.string.toCart),
                        style = AppTheme.typography.regular.copy(
                            color = Color.White,
                            textAlign = TextAlign.Center,
                        )
                    )

                    Spacer(modifier = Modifier.width(AppTheme.dimension.small))

                    Icon(
                        modifier = Modifier.size(AppTheme.dimension.normal),
                        painter = painterResource(R.drawable.cart_passive),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NSSMenuCardDisplayPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(
            modifier = Modifier.background(color = AppTheme.color.background)
        ) {
            NNSMenuCard(
                modifier = modifier,
                title = "Pastry Surprise Box",
                url = "",
                collectTill = "7 PM",
                allergens = false,
                grams = 200,
                picture = ""
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun NSSMenuCardDisplayDarkPreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(
            modifier = Modifier.background(color = AppTheme.color.background)
        ) {
            NNSMenuCard(
                modifier = modifier,
                title = "Pastry Surprise Box",
                url = "",
                collectTill = "7 PM",
                allergens = true,
                grams = 200,
                picture = ""
            )

        }
    }
}