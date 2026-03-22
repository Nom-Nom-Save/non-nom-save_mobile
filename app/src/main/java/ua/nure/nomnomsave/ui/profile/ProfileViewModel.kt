package ua.nure.nomnomsave.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.profile.ProfileRepository
import ua.nure.nomnomsave.repository.resource.ResourceRepository
import ua.nure.nomnomsave.repository.token.TokenRepository
import ua.nure.nomnomsave.ui.profile.Profile.Event.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val resourceRepository: ResourceRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(Profile.State())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<Profile.Event>()
    val event = _event.asSharedFlow()

    private var saveChangesJob: Job? = null

    fun onAction(action: Profile.Action) = viewModelScope.launch {
        when(action) {
            Profile.Action.OnBack -> _event.emit(Profile.Event.OnBack)
            is Profile.Action.OnNavigate -> _event.emit(OnNavigate(route = action.route))

            is Profile.Action.OnAvatarChange -> _state.update { s ->
                s.copy(
                    profile = state.value.profile?.copy(
                        avatarUrl = action.avatarUrl
                    ),
                    showChangeAvatarDialog = false
                )
            }
            Profile.Action.OnDismissChangeAvatarDialog -> {
                _state.update { s ->
                    s.copy(
                        showChangeAvatarDialog = false
                    )
                }
            }
            Profile.Action.OnShowChangeAvatarDialog -> {
                _state.update { s ->
                    s.copy(
                        showChangeAvatarDialog = true
                    )
                }
            }

            is Profile.Action.OnEmailChange -> _state.update { s ->
                s.copy(
                    profile = state.value.profile?.copy(
                        email = action.email
                    )
                )
            }
            is Profile.Action.OnNameChange -> _state.update { s ->
                s.copy(
                    profile = state.value.profile?.copy(
                        fullName = action.name
                    )
                )
            }

            Profile.Action.OnCloseTimeChange -> _state.update { s->
                s.copy(
                    closedTimeNotifications = !state.value.closedTimeNotifications
                )
            }
            Profile.Action.OnNearbyDealsChange -> _state.update { s->
                s.copy(
                    nearbyDealsNotifications = !state.value.nearbyDealsNotifications
                )
            }

            Profile.Action.OnSave -> saveChanges(
                name = state.value.profile?.fullName,
                email = state.value.profile?.email,
            )
        }
    }

    private fun saveChanges(
        name: String?,
        email: String?,
    ) {
        if (name?.isEmpty() == true) {
            _state.update { s->
                s.copy(nameError = resourceRepository.getStringByResource(R.string.nameIsEmpty))
            }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email ?: "").matches()) {
            _state.update { s ->
                s.copy(
                    emailError = resourceRepository.getStringByResource(R.string.emailNotValid)
                )
            }
            return
        }
        saveChangesJob?.cancel()
        saveChangesJob = viewModelScope.launch {
            profileRepository.patchMe(
                fullName = name,
                email = email
            ).onSuccess {}
        }

    }
}