package ua.nure.nomnomsave.ui.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.user.UserRepository
import ua.nure.nomnomsave.ui.favorite.Favorite.Event.*
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    val _state = MutableStateFlow(Favorite.State())
    val state = _state.onStart {
        loadFavorites()
        observeFavorites()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Favorite.State()
    )

    val _event = MutableSharedFlow<Favorite.Event>()
    val event = _event.asSharedFlow()

    var loadFavoriteJob: Job? = null
    var getFavoritesJob: Job? = null

    fun onAction(action: Favorite.Action) = viewModelScope.launch {
        when(action) {
            Favorite.Action.OnBack -> _event.emit(OnBack)
            is Favorite.Action.OnNavigate -> _event.emit(OnNavigate(route = action.route))
            is Favorite.Action.OnQueryChanged -> {
                _state.update { s ->
                    s.copy(
                        query = action.query
                    )
                }
            }

            is Favorite.Action.OnDeleteFavorite -> {
                deleteFromFavorites(
                    favoriteId = action.favoriteId,
                    establishmentId = action.establishmentId
                )
            }
        }
    }

    private fun loadFavorites() {
        loadFavoriteJob?.cancel()
        loadFavoriteJob = viewModelScope.launch {
            userRepository.favorites().onSuccess {
            }
        }
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

    private fun deleteFromFavorites(favoriteId: String, establishmentId: String) = viewModelScope.launch {
        userRepository.deleteFromFavorites(
            favoriteId = favoriteId,
            establishmentId = establishmentId
        )
    }

}