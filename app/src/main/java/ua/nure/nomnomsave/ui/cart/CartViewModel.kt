package ua.nure.nomnomsave.ui.cart

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
import ua.nure.nomnomsave.config.qrCodeBitmapDefaultSize
import ua.nure.nomnomsave.repository.order.OrderRepository
import ua.nure.nomnomsave.repository.resource.ResourceRepository
import ua.nure.nomnomsave.ui.cart.Cart.Event.*
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val resourceRepository: ResourceRepository,
) : ViewModel() {
    private val TAG by lazy { CartViewModel::class.simpleName }

    val _state = MutableStateFlow(Cart.State())
    val state = _state.onStart {
        loadOrders()
        observeOrders()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Cart.State()
    )

    val _event = MutableSharedFlow<Cart.Event>()
    val event = _event.asSharedFlow()

    private var loadOrdersJob: Job? = null
    private var observeOrdersJob: Job? = null

    fun onAction(action: Cart.Action) = viewModelScope.launch {
        when (action) {
            Cart.Action.OnBack -> _event.emit(OnBack)
            is Cart.Action.OnNavigate -> _event.emit(OnNavigate(route = action.route))

            is Cart.Action.OnQueryChanged -> {
                _state.update { s -> s.copy(query = action.query) }
            }

            is Cart.Action.OnTabSelected -> {
                _state.update { s -> s.copy(selectedTab = action.tab) }
            }

            is Cart.Action.OnQR -> {
                _state.update { s ->
                    s.copy(
                        showQRCodeDialog = true,
                        qrCodeDialogTitle = action.title,
                        qrBitmap = resourceRepository.generateQR(action.data, qrCodeBitmapDefaultSize),
                    )
                }
            }

            is Cart.Action.OnDismissQRCodeDialog -> {
                _state.update { s ->
                    s.copy(
                        showQRCodeDialog = false,
                        qrCodeDialogTitle = null,
                        qrBitmap = null,
                    )
                }
            }

            is Cart.Action.OnDeleteOrder -> cancelOrder(action.id)
        }
    }

    private fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.cancelOrder(orderId).let { result ->
                when (result) {
                    is ua.nure.nomnomsave.repository.Result.Success -> loadOrders()
                    is ua.nure.nomnomsave.repository.Result.Error -> {
                        Log.e(TAG, "Failed to cancel order: ${result.error}")
                    }
                }
            }
        }
    }

    private fun loadOrders(page: Int = 1, limit: Int = 10) {
        loadOrdersJob?.cancel()
        loadOrdersJob = viewModelScope.launch {
            orderRepository.orders(page, limit)
        }
    }

    private fun observeOrders() {
        observeOrdersJob?.cancel()
        observeOrdersJob = viewModelScope.launch {
            orderRepository.getOrders().collect { list ->
                Log.d(TAG, "observeOrders: ${list.firstOrNull()?.orderEntity?.id}")
                _state.update { s -> s.copy(orders = list) }
            }
        }
    }
}