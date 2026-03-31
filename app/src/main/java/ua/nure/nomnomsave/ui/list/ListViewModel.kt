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

    private val _state = MutableStateFlow(List.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<List.Event>()
    val event = _event.asSharedFlow()

    init {
        observeEstablishments()
        refreshEstablishments()
        observeFavorites()
    }

    private var updateFavoriteJob: Job? = null
    private var getFavoritesJob: Job? = null

    private fun observeEstablishments() {
        establishmentRepository.getAllEstablishmentsFlow()
            .onEach { establishments ->
                _state.update { s ->
                    s.copy(
                        establishments = establishments,
                        filteredEstablishments = applyFilters(establishments, s)
                    )
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

    fun onAction(action: List.Action) = viewModelScope.launch {
        when (action) {
            is List.Action.OnNavigate -> _event.emit(List.Event.OnNavigate(route = action.route))

            is List.Action.OnSearchChange -> {
                _state.update { s ->
                    val updated = s.copy(searchQuery = action.query)
                    updated.copy(filteredEstablishments = applyFilters(s.establishments, updated))
                }
            }

            is List.Action.OnFavoriteToggle -> {
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

            List.Action.OnShowFilters -> {
                _state.update { s ->
                    s.copy(
                        showFilters = true,
                        pendingMaxDistanceKm = s.maxDistanceKm,
                        pendingMinRating = s.minRating,
                        pendingTimeFilter = s.selectedTimeFilter,
                    )
                }
            }

            List.Action.OnDismissFilters -> _state.update { it.copy(showFilters = false) }

            List.Action.OnApplyFilters -> {
                _state.update { s ->
                    val updated = s.copy(
                        showFilters = false,
                        maxDistanceKm = s.pendingMaxDistanceKm,
                        minRating = s.pendingMinRating,
                        selectedTimeFilter = s.pendingTimeFilter,
                    )
                    updated.copy(filteredEstablishments = applyFilters(s.establishments, updated))
                }
            }

            is List.Action.OnDistanceChange -> _state.update { it.copy(pendingMaxDistanceKm = action.km) }

            is List.Action.OnRatingChange -> _state.update { it.copy(pendingMinRating = action.rating) }

            is List.Action.OnTimeFilterChange -> {
                _state.update { s ->
                    val toggled = if (s.pendingTimeFilter == action.option) null else action.option
                    s.copy(pendingTimeFilter = toggled)
                }
            }

            is List.Action.OnSortChange -> {
                _state.update { s ->
                    val updated = s.copy(selectedSort = action.sort)
                    updated.copy(filteredEstablishments = applyFilters(s.establishments, updated))
                }
            }
        }
    }

    private fun applyFilters(
        establishments: kotlin.collections.List<EstablishmentEntity>,
        state: List.State,
    ): kotlin.collections.List<EstablishmentEntity> {
        return establishments
            .filter { entity ->
                val matchesSearch = state.searchQuery.isBlank() ||
                        entity.name?.contains(state.searchQuery, ignoreCase = true) == true ||
                        entity.adress?.contains(state.searchQuery, ignoreCase = true) == true
                val matchesRating = entity.rating?.toFloatOrNull()
                    ?.let { it >= state.minRating } ?: true
                matchesSearch && matchesRating
            }
            .let { list ->
                when (state.selectedSort) {
                    List.SortOption.RATING_DESC -> list.sortedByDescending { it.rating?.toFloatOrNull() }
                    List.SortOption.RATING_ASC -> list.sortedBy { it.rating?.toFloatOrNull() }
                    else -> list
                }
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