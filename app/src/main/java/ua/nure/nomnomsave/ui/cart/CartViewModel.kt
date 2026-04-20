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
            is Cart.Action.OnCreateOrder -> createOrderFromMenuItem(action.menuPriceId, action.quantity)
            is Cart.Action.OnAddToLocalCart -> addToLocalCart(action.item)
            is Cart.Action.OnRemoveFromLocalCart -> removeFromLocalCart(action.menuPriceId)
            is Cart.Action.OnOrderSingleItem -> submitSingleItem(action.menuPriceId, action.quantity)
            Cart.Action.OnSubmitLocalOrder -> submitLocalOrder()
            is Cart.Action.OnOrderAllFromEstablishment -> orderAllFromEstablishment(action.establishmentName)
        }
    }

    private fun addToLocalCart(item: LocalCartItem) {
        _state.update { s ->
            val existing = s.localCartItems.find { it.detail.menuPriceId == item.detail.menuPriceId }
            val updated = if (existing != null) {
                s.localCartItems.map { 
                    if (it.detail.menuPriceId == item.detail.menuPriceId) {
                        it.copy(detail = it.detail.copy(quantity = it.detail.quantity + item.detail.quantity))
                    } else it
                }
            } else {
                s.localCartItems + item
            }
            s.copy(localCartItems = updated)
        }
    }

    private fun removeFromLocalCart(menuPriceId: String) {
        _state.update { s ->
            s.copy(localCartItems = s.localCartItems.filter { it.detail.menuPriceId != menuPriceId })
        }
    }

    private fun submitSingleItem(menuPriceId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(inProgress = true) }
                
                val createOrderRequest = ua.nure.nomnomsave.repository.dto.CreateOrderRequest(
                    items = listOf(
                        ua.nure.nomnomsave.repository.dto.OrderItemRequest(
                            menuPriceId = menuPriceId,
                            quantity = quantity
                        )
                    )
                )
                
                Log.d("CartViewModel", "DEBUG submitSingleItem: request=$createOrderRequest")
                
                orderRepository.createOrder(createOrderRequest).let { result ->
                    when (result) {
                        is ua.nure.nomnomsave.repository.Result.Success -> {
                            Log.d(TAG, "Order submitted successfully: ${result.data.id}")
                            _state.update { s ->
                                s.copy(
                                    inProgress = false,
                                    localCartItems = s.localCartItems.filter { it.detail.menuPriceId != menuPriceId }
                                )
                            }
                            loadOrders()
                        }
                        is ua.nure.nomnomsave.repository.Result.Error -> {
                            Log.e(TAG, "Failed to submit order: ${result.error}")
                            _state.update { it.copy(inProgress = false) }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}", e)
                _state.update { it.copy(inProgress = false) }
            }
        }
    }

    private fun submitLocalOrder() {
        viewModelScope.launch {
            try {
                val items = _state.value.localCartItems
                if (items.isEmpty()) {
                    return@launch
                }

                _state.update { it.copy(inProgress = true) }
                
                val createOrderRequest = ua.nure.nomnomsave.repository.dto.CreateOrderRequest(
                    items = items.map { item ->
                        ua.nure.nomnomsave.repository.dto.OrderItemRequest(
                            menuPriceId = item.detail.menuPriceId,
                            quantity = item.detail.quantity
                        )
                    }
                )

                orderRepository.createOrder(createOrderRequest).let { result ->
                    when (result) {
                        is ua.nure.nomnomsave.repository.Result.Success -> {
                            _state.update { it.copy(inProgress = false, localCartItems = emptyList()) }
                            loadOrders()
                        }
                        is ua.nure.nomnomsave.repository.Result.Error -> {
                            _state.update { it.copy(inProgress = false) }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(inProgress = false) }
            }
        }
    }

    private fun orderAllFromEstablishment(establishmentName: String) {
        viewModelScope.launch {
            try {
                val items = _state.value.localCartItems.filter { it.establishmentName == establishmentName }
                if (items.isEmpty()) {
                    return@launch
                }

                _state.update { it.copy(inProgress = true) }
                
                val createOrderRequest = ua.nure.nomnomsave.repository.dto.CreateOrderRequest(
                    items = items.map { item ->
                        ua.nure.nomnomsave.repository.dto.OrderItemRequest(
                            menuPriceId = item.detail.menuPriceId,
                            quantity = item.detail.quantity
                        )
                    }
                )

                orderRepository.createOrder(createOrderRequest).let { result ->
                    when (result) {
                        is ua.nure.nomnomsave.repository.Result.Success -> {
                            Log.d(TAG, "Order from establishment created successfully: ${result.data.id}")
                            _state.update { s ->
                                s.copy(
                                    inProgress = false,
                                    localCartItems = s.localCartItems.filter { it.establishmentName != establishmentName }
                                )
                            }
                            loadOrders()
                        }
                        is ua.nure.nomnomsave.repository.Result.Error -> {
                            Log.e(TAG, "Failed to create order from establishment: ${result.error}")
                            _state.update { it.copy(inProgress = false) }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while creating order from establishment: ${e.message}", e)
                _state.update { it.copy(inProgress = false) }
            }
        }
    }

    private fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                orderRepository.cancelOrder(orderId).let { result ->
                    when (result) {
                        is ua.nure.nomnomsave.repository.Result.Success -> {
                            Log.d(TAG, "Order cancelled successfully: $orderId")
                            loadOrders()
                        }
                        is ua.nure.nomnomsave.repository.Result.Error -> {
                            Log.e(TAG, "Failed to cancel order: ${result.error}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while cancelling order: ${e.message}", e)
            }
        }
    }

    private fun createOrderFromMenuItem(menuPriceId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(inProgress = true) }
                
                val createOrderRequest = ua.nure.nomnomsave.repository.dto.CreateOrderRequest(
                    items = listOf(
                        ua.nure.nomnomsave.repository.dto.OrderItemRequest(
                            menuPriceId = menuPriceId,
                            quantity = quantity
                        )
                    )
                )
                orderRepository.createOrder(createOrderRequest).let { result ->
                    when (result) {
                        is ua.nure.nomnomsave.repository.Result.Success -> {
                            Log.d(TAG, "Order created successfully: ${result.data.id}")
                            _state.update { it.copy(inProgress = false) }
                            loadOrders()
                        }
                        is ua.nure.nomnomsave.repository.Result.Error -> {
                            Log.e(TAG, "Failed to create order: ${result.error}")
                            _state.update { it.copy(inProgress = false) }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while creating order: ${e.message}", e)
                _state.update { it.copy(inProgress = false) }
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