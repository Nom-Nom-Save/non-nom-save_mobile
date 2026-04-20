package ua.nure.nomnomsave.ui.establishmentDetails

import android.view.Menu
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.db.data.entity.ReviewEntity
import ua.nure.nomnomsave.navigation.Screen

object EstablishmentDetails {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
        data class OnAddToCart(val menuItem: MenuEntity, val quantity: Int) : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data class OnOpenReviewSheet(val review: ReviewEntity? = null) : Action
        data object OnCloseReviewSheet : Action
        data class OnSubmitReview(val rating: Int, val comment: String) : Action
        data class OnDeleteReview(val reviewId: String) : Action
        data class OnReserveNow(val menuItem: MenuEntity, val quantity: Int) : Action
    }

    data class State(
        val establishment: EstablishmentEntity? = null,
        val inProgress: Boolean = false,
        val menu: List<MenuEntity>? = null,
        val reviews: List<ReviewEntity> = emptyList(),
        val showReviewSheet: Boolean = false,
        val editingReview: ReviewEntity? = null
        )
}