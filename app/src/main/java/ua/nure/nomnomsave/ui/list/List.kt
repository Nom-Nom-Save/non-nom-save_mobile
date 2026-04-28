package ua.nure.nomnomsave.ui.list

import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.entity.Favorite
import ua.nure.nomnomsave.db.data.entity.FavoriteEntity
import ua.nure.nomnomsave.navigation.Screen

object ListContract {
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
        data class OnSortDirectionChange(val direction: SortDirection) : Action
        data class OnCityChange(val city: String) : Action
        data class OnProductTypesChange(val typeIds: List<String>) : Action
    }

    data class State(
        val establishments: List<EstablishmentEntity> = emptyList(),
        val filteredEstablishments: List<EstablishmentEntity> = emptyList(),
        val searchQuery: String = "",
        val isLoading: Boolean = false,
        val showFilters: Boolean = false,
        val favorites: List<Favorite>? = null,
        // Active filters
        val maxDistanceKm: Float? = null,
        val minRating: Float? = null,
        val selectedTimeFilter: TimeFilterOption? = null,
        val selectedSort: SortOption = SortOption.DISTANCE,
        val sortDirection: SortDirection = SortDirection.ASCENDING,
        val selectedCity: String = "All cities",
        val selectedProductTypes: List<String> = emptyList(),
        val userLat: Double? = null,
        val userLon: Double? = null,
        // Pending filters (inside bottom sheet,before Apply)
        val pendingMaxDistanceKm: Float? = null,
        val pendingMinRating: Float? = null,
        val pendingTimeFilter: TimeFilterOption? = null,
        val pendingCity: String = "All cities",
        val pendingProductTypes: List<String> = emptyList(),
        val availableCities: List<String> = listOf("Kyiv", "Lviv", "Odesa", "Kharkiv", "Dnipro"),
        val availableProductTypes: Map<String, String> = emptyMap(),
    )

    enum class TimeFilterOption(val label: String) {
        WITHIN_1_HOUR("Within 1 hour"),
        WITHIN_2_HOURS("Within 2 hours"),
    }

    enum class SortOption(val backendValue: String) {
        DISTANCE("distance"),
        RATING("rating"),
        CLOSING_TIME("closingTime");
    }

    enum class SortDirection {
        ASCENDING,
        DESCENDING;

        fun toggle(): SortDirection = if (this == ASCENDING) DESCENDING else ASCENDING
    }
}