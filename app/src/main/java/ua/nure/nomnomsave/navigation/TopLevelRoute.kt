package ua.nure.nomnomsave.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ua.nure.nomnomsave.R

data class TopLevelRoute<T: Any>(
    val route: T,
    @param:StringRes val title: Int,
    @param:DrawableRes val selectedIcon: Int,
    @param:DrawableRes val unselectedIcon: Int,
)

val topLevelRoutes = listOf<TopLevelRoute<NestedGraph>>(
    TopLevelRoute(
        route = NestedGraph.Profile,
        title = R.string.profile,
        selectedIcon = R.drawable.profile_active,
        unselectedIcon = R.drawable.profile_passive
    ),
//    TopLevelRoute(
//        route = NestedGraph.Maps,
//        title = R.string.maps,
//        selectedIcon = R.drawable.maps_active,
//        unselectedIcon = R.drawable.maps_passive
//    ),
//    TopLevelRoute(
//        route = NestedGraph.Cart,
//        title = R.string.cart,
//        selectedIcon = R.drawable.cart_active,
//        unselectedIcon = R.drawable.cart_passive
//    ),
//    TopLevelRoute(
//        route = NestedGraph.List,
//        title = R.string.list,
//        selectedIcon = R.drawable.list_active,
//        unselectedIcon = R.drawable.list_passive
//    ),
//    TopLevelRoute(
//        route = NestedGraph.Favorites,
//        title = R.string.favorites,
//        selectedIcon = R.drawable.favorite_active,
//        unselectedIcon = R.drawable.favorite_passive
//    ),

)