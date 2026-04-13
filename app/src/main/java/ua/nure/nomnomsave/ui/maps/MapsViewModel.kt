package ua.nure.nomnomsave.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.repository.establishment.EstablishmentRepository
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val establishmentRepository: EstablishmentRepository
) : ViewModel() {
    private val _state = MutableStateFlow(Maps.State())
    val state = _state.onStart {
        loadEstablishments()
    }
    private val _event = MutableSharedFlow<Maps.Event>()
    val event = _event.asSharedFlow()
    private var loadEstablishmentsJob: Job? = null

    fun onAction(action: Maps.Action) = viewModelScope.launch {
        when(action) {
            Maps.Action.OnBack -> _event.emit(Maps.Event.OnBack)
            is Maps.Action.OnNavigate -> _event.emit(Maps.Event.OnNavigate(route = action.route))
            is Maps.Action.OnEstablishmentSelected -> {
                _state.update { s ->
                    s.copy(
                        selectedEstablishment = action.establishment
                    )
                }
            }
        }
    }

    private fun loadEstablishments() {
        loadEstablishmentsJob?.cancel()

        loadEstablishmentsJob = viewModelScope.launch {
            establishmentRepository.getAllEstablishmentsFlow()
                .collect { list ->
                    _state.update {
                        it.copy(establishments = list)
                    }
                }
        }

    }
}


