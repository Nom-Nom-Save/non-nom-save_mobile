package ua.nure.nomnomsave.ui.cart

import android.graphics.Bitmap
import ua.nure.nomnomsave.db.data.entity.Order
import ua.nure.nomnomsave.navigation.Screen

object Cart {
    sealed interface Event {
        data class OnNavigate(val route: Screen) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnNavigate(val route: Screen) : Action
        data class OnQueryChanged(val query: String) : Action
        data class OnQR(val data: String, val title: String?) : Action
        data class OnDismissQRCodeDialog(val state: Boolean) : Action
        data class OnDeleteOrder(val id: String) : Action
        data class OnTabSelected(val tab: Tab) : Action
    }

    enum class Tab { ORDER, MY_ORDERS }

    data class State(
        val inProgress: Boolean = false,
        val query: String? = null,
        val orders: List<Order>? = null,
        val showQRCodeDialog: Boolean = false,
        val qrCodeDialogTitle: String? = null,
        val qrBitmap: Bitmap? = null,
        val selectedTab: Tab = Tab.ORDER,
    )
}