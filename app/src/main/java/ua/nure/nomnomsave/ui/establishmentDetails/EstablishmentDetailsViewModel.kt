package ua.nure.nomnomsave.ui.establishmentDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EstablishmentDetailsViewModel @Inject constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(EstablishmentDetails.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EstablishmentDetails.Event>()
    val event = _event.asSharedFlow()

    fun onAction(action: EstablishmentDetails.Action) = viewModelScope.launch {
        when (action) {
            EstablishmentDetails.Action.OnBack -> {
                _event.emit(EstablishmentDetails.Event.OnBack)
            }
            is EstablishmentDetails.Action.OnNavigate -> {
                _event.emit(EstablishmentDetails.Event.OnNavigate(route = action.route))
            }
        }
    }
}