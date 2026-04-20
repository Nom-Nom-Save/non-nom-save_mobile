package ua.nure.nomnomsave.ui.premium

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ua.nure.nomnomsave.repository.token.TokenRepository
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {
    private val _state = MutableStateFlow(Premium.State(
        token = tokenRepository.token
    ))
    val state = _state.asStateFlow()

}