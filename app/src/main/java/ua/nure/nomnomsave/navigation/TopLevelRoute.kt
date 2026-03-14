package ua.nure.nomnomsave.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TopLevelRoute<T: Any>(
    val route: T,
    @param:StringRes val title: Int,
    @param:DrawableRes val selectedIcon: Int,
    @param:DrawableRes val unselectedIcon: Int,
)

val topLevelRoutes = listOf<TopLevelRoute<NestedGraph>>(
//    TopLevelRoute(
//        route = NestedGraph.Dashboard,
//        title = R.string.dashboard,
//        selectedIcon = R.drawable.profile_active,
//        unselectedIcon = R.drawable.profile_passive
//    ),
//    TopLevelRoute(
//        route = NestedGraph.OwnTrainer,
//        title = R.string.ownTrainer,
//        selectedIcon = R.drawable.own_trainer_active,
//        unselectedIcon = R.drawable.own_trainer_passive
//    ),
//    TopLevelRoute(
//        route = NestedGraph.Trainer,
//        title = R.string.trainer,
//        selectedIcon = R.drawable.trainer_active,
//        unselectedIcon = R.drawable.trainer_passive
//    ),
//    TopLevelRoute(
//        route = NestedGraph.Analytics,
//        title = R.string.analytics,
//        selectedIcon = R.drawable.analytics_active,
//        unselectedIcon = R.drawable.analytics_passive
//    ),
//    TopLevelRoute(
//        route = NestedGraph.Chat,
//        title = R.string.chat,
//        selectedIcon = R.drawable.chat_active,
//        unselectedIcon = R.drawable.chat_passive
//    ),

)