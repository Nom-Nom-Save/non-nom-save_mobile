package ua.nure.nomnomsave.ui.compose.navBar

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.nure.nomnomsave.navigation.NestedGraph
import ua.nure.nomnomsave.navigation.TopLevelRoute
import ua.nure.nomnomsave.navigation.topLevelRoutes
import ua.nure.nomnomsave.ui.theme.AppTheme

@Composable
fun NNSNavigationBarItem(
    item: TopLevelRoute<NestedGraph>,
    isSelected: Boolean = false,
    onItemSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(all = AppTheme.dimension.small)
            .clip(shape = AppTheme.shape.accentShape)
            .clickable {
                onItemSelect()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(if(isSelected) item.selectedIcon else item.unselectedIcon),
            contentDescription = null,
            tint = if (isSelected) AppTheme.color.accent else AppTheme.color.grey
        )
        Text(
            modifier = Modifier.padding(top = 4.dp)
                .padding(horizontal = AppTheme.dimension.small)
            ,
            text = stringResource(item.title),
            style = AppTheme.typography.small.copy(
                fontSize = 12.sp,
                color = if(isSelected) AppTheme.color.accent else AppTheme.color.grey
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NNSNavigationBarItemActivePreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = Modifier.background(color = AppTheme.color.background)) {
            NNSNavigationBarItem(
                item = topLevelRoutes[0],
                isSelected = true
            ) {}

        }
    }
}
@Preview(showBackground = true)
@Composable
private fun NNSNavigationBarItemPassivePreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = Modifier.background(color = AppTheme.color.background)) {
            NNSNavigationBarItem(
                item = topLevelRoutes[0],
                isSelected = false
            ) {}
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NNSNavigationBarItemDarkActivePreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = Modifier.background(color = AppTheme.color.background)) {
            NNSNavigationBarItem(
                item = topLevelRoutes[0],
                isSelected = true
            ) {}

        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NNSNavigationBarItemDarkPassivePreview(modifier: Modifier = Modifier) {
    AppTheme {
        Box(modifier = Modifier.background(color = AppTheme.color.background)) {
            NNSNavigationBarItem(
                item = topLevelRoutes[0],
                isSelected = false
            ) {}
        }
    }
}