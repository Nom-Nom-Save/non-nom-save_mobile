package ua.nure.nomnomsave.ui.list

import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.navigation.Screen

object List {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
    }

    sealed interface Action {
        data class OnNavigate(val route: Screen) : Action
        data class OnSearchChange(val query: String) : Action
        data class OnFavoriteToggle(val id: String) : Action
        data object OnShowFilters : Action
        data object OnDismissFilters : Action
        data object OnApplyFilters : Action
        data class OnDistanceChange(val km: Float) : Action
        data class OnRatingChange(val rating: Float) : Action
        data class OnTimeFilterChange(val option: TimeFilterOption) : Action
        data class OnSortChange(val sort: SortOption) : Action
    }

    data class State(
        val establishments: kotlin.collections.List<EstablishmentEntity> = emptyList(),
        val filteredEstablishments: kotlin.collections.List<EstablishmentEntity> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val showFilters: Boolean = false,
        val favorites: Set<String> = emptySet(),
        // Active filters
        val maxDistanceKm: Float = 8f,
        val minRating: Float = 1f,
        val selectedTimeFilter: TimeFilterOption? = null,
        val selectedSort: SortOption = SortOption.NONE,
        // Pending filters (inside bottom sheet, before Apply)
        val pendingMaxDistanceKm: Float = 8f,
        val pendingMinRating: Float = 1f,
        val pendingTimeFilter: TimeFilterOption? = null,
    )

    enum class TimeFilterOption(val label: String) {
        WITHIN_1_HOUR("Within 1 hour"),
        WITHIN_2_HOURS("Within 2 hours"),
    }

    enum class SortOption {
        NONE, PRICE_ASC, PRICE_DESC, DISTANCE_ASC, DISTANCE_DESC, RATING_ASC, RATING_DESC
    }
}