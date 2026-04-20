package ua.nure.nomnomsave.ui.establishmentDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // <-- Добавлен импорт update!
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.db.data.mappers.toEntity
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.repository.establishment.EstablishmentRepository
import ua.nure.nomnomsave.repository.menu.MenuRepository
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.review.ReviewRepository
import javax.inject.Inject

@HiltViewModel
class EstablishmentDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: EstablishmentRepository,
    private val menuRepository: MenuRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EstablishmentDetails.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EstablishmentDetails.Event>()
    val event = _event.asSharedFlow()

    init {
        val navArgs = savedStateHandle.toRoute<Screen.List.EstablishmentDetails>()
        val establishmentId = navArgs.id
        loadData(establishmentId)
    }

    private fun loadData(id: String) {
        _state.update { it.copy(inProgress = true) }

        viewModelScope.launch {
            menuRepository.getMenuByEstablishment(id).collect { menuList ->
                _state.update { it.copy(menu = menuList) }
            }
        }

        viewModelScope.launch {
            reviewRepository.getLocalReviews(id).collect { reviewList ->
                _state.update { it.copy(reviews = reviewList) }
            }
        }

        viewModelScope.launch {
            repository.getEstablishmentById(id).onSuccess { dto ->
                _state.update { it.copy(
                    establishment = dto.toEntity(),
                    inProgress = false
                )}
            }
        }

        viewModelScope.launch {
            menuRepository.fetchMenu(id)
        }

        viewModelScope.launch {
            reviewRepository.fetchReviews(id)
        }
    }

    fun onAction(action: EstablishmentDetails.Action) = viewModelScope.launch {
        when (action) {
            EstablishmentDetails.Action.OnBack -> {
                _event.emit(EstablishmentDetails.Event.OnBack)
            }
            is EstablishmentDetails.Action.OnNavigate -> {
                _event.emit(EstablishmentDetails.Event.OnNavigate(route = action.route))
            }
            is EstablishmentDetails.Action.OnOpenReviewSheet -> {
                _state.update { it.copy(showReviewSheet = true, editingReview = action.review) }
            }
            EstablishmentDetails.Action.OnCloseReviewSheet -> {
                _state.update { it.copy(showReviewSheet = false, editingReview = null) }
            }
            is EstablishmentDetails.Action.OnSubmitReview -> {
                _state.update { it.copy(showReviewSheet = false) }

                val estId = state.value.establishment?.id ?: return@launch
                val editingReview = state.value.editingReview

                if (editingReview != null) {
                    reviewRepository.updateReview(
                        reviewId = editingReview.id,
                        rating = action.rating,
                        comment = action.comment
                    )
                } else {
                    reviewRepository.createReview(
                        establishmentId = estId,
                        rating = action.rating,
                        comment = action.comment
                    )
                }
                _state.update { it.copy(editingReview = null) }
            }
            is EstablishmentDetails.Action.OnDeleteReview -> {
                reviewRepository.deleteReview(action.reviewId)
            }
            is EstablishmentDetails.Action.OnReserveNow -> {
                _event.emit(EstablishmentDetails.Event.OnAddToCart(
                    menuItem = action.menuItem,
                    quantity = action.quantity
                ))
            }
        }
    }
}