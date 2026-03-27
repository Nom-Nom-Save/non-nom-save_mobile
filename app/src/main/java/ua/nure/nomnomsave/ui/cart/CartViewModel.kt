package ua.nure.nomnomsave.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.repository.order.OrderRepository
import ua.nure.nomnomsave.ui.cart.Cart.Event.*
import javax.inject.Inject

@HiltViewModel
class CartViewModel  @Inject constructor(
    private val orderRepository: OrderRepository,
): ViewModel() {
    val _state = MutableStateFlow(Cart.State())
    val state = _state.onStart {
        orderRepository.orders()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Cart.State()
    )

    val _event = MutableSharedFlow<Cart.Event>()
    val event = _event.asSharedFlow()

    fun onAction(action: Cart.Action) = viewModelScope.launch {
        when(action) {
            Cart.Action.OnBack -> _event.emit(Cart.Event.OnBack)
            is Cart.Action.OnNavigate -> _event.emit(OnNavigate(route = action.route))
            is Cart.Action.OnQueryChanged -> {
                _state.update { s ->
                    s.copy(
                        query = action.query
                    )
                }
            }
        }
    }


}