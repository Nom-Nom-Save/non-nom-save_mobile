package ua.nure.nomnomsave.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.mappers.toEntity
import ua.nure.nomnomsave.repository.establishment.EstablishmentRepository
import ua.nure.nomnomsave.repository.onError
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.user.UserRepository
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val establishmentRepository: EstablishmentRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val TAG by lazy { ListViewModel::class.simpleName }

    private val _state = MutableStateFlow(ListContract.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ListContract.Event>()
    val event = _event.asSharedFlow()

    init {
        observeEstablishments()
        refreshEstablishments()
        observeFavorites()
        loadProductTypes()
        loadCities()
    }

    private var getFavoritesJob: Job? = null

    private fun loadCities() {
        viewModelScope.launch {
            establishmentRepository.getCities()
                .onSuccess { cities ->
                    _state.update { it.copy(availableCities = cities) }
                }
                .onError { error ->
                    Log.e(TAG, "Failed to load cities: $error")
                }
        }
    }

    private fun loadProductTypes() {
        viewModelScope.launch {
            establishmentRepository.getProductTypes()
                .onSuccess { productTypes ->
                    _state.update { it.copy(availableProductTypes = productTypes) }
                }
                .onError { error ->
                    Log.e(TAG, "Failed to load product types: $error")
                }
        }
    }

    private fun observeEstablishments() {
        establishmentRepository.getAllEstablishmentsFlow()
            .onEach { establishments ->
                _state.update { s ->
                    val hasActiveFilters = s.selectedProductTypes.isNotEmpty() ||
                            (s.selectedCity != "All cities" && s.selectedCity != "My location") ||
                            s.minRating != null ||
                            s.maxDistanceKm != null
                    
                    if (hasActiveFilters) {
                        Log.d(TAG, "Active filters detected, skipping Flow update")
                        s.copy(establishments = establishments)
                    } else {
                        Log.d(TAG, "No active filters, updating from Flow")
                        s.copy(
                            establishments = establishments,
                            filteredEstablishments = applyFilters(establishments, s)
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshEstablishments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            establishmentRepository.getAllEstablishments()
                .onSuccess { Log.d(TAG, "Establishments refreshed") }
                .onError { Log.e(TAG, "Refresh error: $it") }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onAction(action: ListContract.Action) = viewModelScope.launch {
        when (action) {
            is ListContract.Action.OnNavigate -> _event.emit(ListContract.Event.OnNavigate(route = action.route))

            is ListContract.Action.OnSearchChange -> {
                _state.update { s ->
                    val updated = s.copy(searchQuery = action.query)
                    updated.copy(filteredEstablishments = applyFilters(s.establishments, updated))
                }
            }

            is ListContract.Action.OnFavoriteToggle -> {
                state.value.favorites?.firstOrNull { it.establishment.id == action.id }
                    ?.let { favorite ->
                        deleteFromFavorites(
                            favoriteId = favorite.favoriteEntity.id,
                            establishmentId = favorite.establishment.id
                        )
                    } ?: run {
                    addToFavorite(establishmentId = action.id)
                }
            }

            ListContract.Action.OnShowFilters -> {
                _state.update { s ->
                    s.copy(
                        showFilters = true,
                        pendingMaxDistanceKm = s.maxDistanceKm,
                        pendingMinRating = s.minRating,
                        pendingTimeFilter = s.selectedTimeFilter,
                        pendingCity = s.selectedCity,
                        pendingProductTypes = s.selectedProductTypes,
                    )
                }
            }

            ListContract.Action.OnDismissFilters -> _state.update { it.copy(showFilters = false) }

            ListContract.Action.OnApplyFilters -> {
                Log.d(TAG, "OnApplyFilters - pendingProductTypes: ${_state.value.pendingProductTypes}")
                _state.update { s ->
                    s.copy(
                        showFilters = false,
                        maxDistanceKm = s.pendingMaxDistanceKm,
                        minRating = s.pendingMinRating,
                        selectedTimeFilter = s.pendingTimeFilter,
                        selectedCity = s.pendingCity,
                        selectedProductTypes = s.pendingProductTypes,
                    )
                }
                refreshEstablishmentsWithFilters()
            }
            is ListContract.Action.OnDistanceChange -> _state.update { it.copy(pendingMaxDistanceKm = action.km) }

            is ListContract.Action.OnRatingChange -> _state.update { it.copy(pendingMinRating = action.rating) }

            is ListContract.Action.OnTimeFilterChange -> {
                _state.update { s ->
                    val toggled = if (s.pendingTimeFilter == action.option) null else action.option
                    s.copy(pendingTimeFilter = toggled)
                }
            }

            is ListContract.Action.OnSortChange -> {
                _state.update { s ->
                    s.copy(
                        selectedSort = action.sort,
                        sortDirection = ListContract.SortDirection.ASCENDING
                    )
                }
                refreshEstablishmentsWithFilters()
            }

            is ListContract.Action.OnSortDirectionChange -> {
                _state.update { s ->
                    s.copy(sortDirection = action.direction)
                }
                refreshEstablishmentsWithFilters()
            }

            is ListContract.Action.OnCityChange -> {
                _state.update { s ->
                    val newCity = if (s.pendingCity == action.city) "All cities" else action.city
                    
                    val newDistanceKm = if (newCity != "My location" && newCity != "All cities") {
                        null
                    } else {
                        s.pendingMaxDistanceKm
                    }
                    
                    s.copy(
                        pendingCity = newCity,
                        pendingMaxDistanceKm = newDistanceKm
                    )
                }
            }

            is ListContract.Action.OnProductTypesChange -> {
                _state.update { it.copy(pendingProductTypes = action.typeIds) }
            }
        }
    }

    private fun applyFilters(
        list: List<EstablishmentEntity>,
        state: ListContract.State,
    ): List<EstablishmentEntity> {
        val filtered = list
            .filter { entity ->
                val matchesSearch = state.searchQuery.isBlank() ||
                        entity.name?.contains(state.searchQuery, ignoreCase = true) == true ||
                        entity.adress?.contains(state.searchQuery, ignoreCase = true) == true
                val matchesRating = state.minRating?.let { minRating ->
                    entity.rating?.toFloatOrNull()?.let { it >= minRating } ?: true
                } ?: true
                matchesSearch && matchesRating
            }

        val sorted = when (state.selectedSort) {
            ListContract.SortOption.RATING -> {
                if (state.sortDirection == ListContract.SortDirection.ASCENDING) {
                    filtered.sortedBy { it.rating?.toFloatOrNull() ?: 0f }
                } else {
                    filtered.sortedByDescending { it.rating?.toFloatOrNull() ?: 0f }
                }
            }
            ListContract.SortOption.DISTANCE -> filtered
            ListContract.SortOption.CLOSING_TIME -> filtered
        }

        return sorted
    }


    private fun refreshEstablishmentsWithFilters() {
        viewModelScope.launch {
            val s = _state.value
            _state.update { it.copy(isLoading = true) }

            Log.d(TAG, "=== API Request ===")
            Log.d(TAG, "City: ${if (s.selectedCity != "All cities" && s.selectedCity != "My location") s.selectedCity else "null (All cities)"}")
            Log.d(TAG, "Lat: ${if (s.selectedCity == "My location") s.userLat else "null"}")
            Log.d(TAG, "Lon: ${if (s.selectedCity == "My location") s.userLon else "null"}")
            Log.d(TAG, "MinRating: ${s.minRating}")
            Log.d(TAG, "Distance: ${s.maxDistanceKm}")
            Log.d(TAG, "ProductTypes: ${s.selectedProductTypes}")
            Log.d(TAG, "SortBy: ${s.selectedSort.backendValue}")
            Log.d(TAG, "SortOrder: ${if (s.sortDirection == ListContract.SortDirection.ASCENDING) "ASC" else "DESC"}")

            establishmentRepository.getFilteredEstablishments(
                city = if (s.selectedCity != "All cities" && s.selectedCity != "My location") s.selectedCity else null,
                lat = if (s.selectedCity == "My location") s.userLat else null,
                lon = if (s.selectedCity == "My location") s.userLon else null,
                radius = s.maxDistanceKm?.toDouble(),
                minRating = s.minRating,
                productTypeIds = s.selectedProductTypes.ifEmpty { null },
                sortBy = s.selectedSort.backendValue,
                sortOrder = if (s.sortDirection == ListContract.SortDirection.ASCENDING) "ASC" else "DESC"
            ).onSuccess { establishments ->
                Log.d(TAG, "API Response: ${establishments.size} establishments received")
                val entityList = establishments.map { it.toEntity() }
                _state.update { state ->
                    val updatedState = state.copy(establishments = entityList)
                    val filtered = applyFilters(entityList, updatedState)
                    Log.d(TAG, "After local filters: ${filtered.size} establishments")
                    updatedState.copy(filteredEstablishments = filtered)
                }
            }
                .onError { error ->
                    Log.e(TAG, "Filter error: $error")
                    _state.update { it.copy(isLoading = false) }
                }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun addToFavorite(establishmentId: String) = viewModelScope.launch {
        userRepository.addToFavorites(
            establishmentId = establishmentId
        )
    }

    private fun deleteFromFavorites(favoriteId: String, establishmentId: String) =
        viewModelScope.launch {
            userRepository.deleteFromFavorites(
                favoriteId = favoriteId,
                establishmentId = establishmentId
            )
        }

    private fun observeFavorites() {
        getFavoritesJob?.cancel()
        getFavoritesJob = viewModelScope.launch {
            userRepository.getFavorites().collect { list ->
                _state.update { s ->
                    s.copy(
                        favorites = list
                    )
                }
            }
        }
    }
}