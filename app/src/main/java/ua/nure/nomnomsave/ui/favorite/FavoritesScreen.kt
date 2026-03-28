package ua.nure.nomnomsave.ui.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.config.dishUrl
import ua.nure.nomnomsave.ui.cart.Cart
import ua.nure.nomnomsave.ui.compose.NNSCartBoxDisplay
import ua.nure.nomnomsave.ui.compose.NNSInputField
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.favorite.compose.FavoriteItem
import ua.nure.nomnomsave.ui.theme.AppTheme
import java.time.LocalDateTime

@Composable
fun FavoritesScreen(
    viewModel: FavoriteViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                Favorite.Event.OnBack -> navController.navigateUp()
                is Favorite.Event.OnNavigate -> navController.navigate(route = it.route)
            }
        }
    }

    FavoritesScreenContent(
        state = state,
        onAction = viewModel::onAction
    )

}

@Composable
private fun FavoritesScreenContent(
    state: Favorite.State,
    onAction: (Favorite.Action) -> Unit
) {
    NNSScreen {
        var search by remember { mutableStateOf(state.query) }
        NNSInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimension.normal),
            label = stringResource(R.string.searchPromt),
            value = search,
        ) {
            search = it
            onAction(Favorite.Action.OnQueryChanged(query = it))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .padding(horizontal = AppTheme.dimension.normal)
                .padding(top = AppTheme.dimension.normal)
        ) {
            items(
                items = state.favorites ?: emptyList(),
                key = { it.favoriteEntity.id }) { favorite ->
                FavoriteItem(
                    modifier = Modifier,
                    url = favorite.establishment.banner ?: favorite.establishment.logo ?: dishUrl,
                    title = favorite.establishment.name ?: "",
                    comment = favorite.establishment.adress ?: "",
                    rating = favorite.establishment.rating ?: ""
                ) {
                    onAction(
                        Favorite.Action.OnDeleteFavorite(
                            favoriteId = favorite.favoriteEntity.id,
                            establishmentId = favorite.establishment.id
                        )
                    )
                }
            }
        }
    }
}