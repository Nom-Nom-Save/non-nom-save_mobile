package ua.nure.nomnomsave.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.navigation.Screen
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.profile.ProfileRepository
import ua.nure.nomnomsave.repository.resource.ResourceRepository
import ua.nure.nomnomsave.ui.profile.Profile.Event.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val resourceRepository: ResourceRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(Profile.State())
    val state = _state.onStart {
        loadMe()
        observeProfile()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Profile.State()
    )

    private val _event = MutableSharedFlow<Profile.Event>()
    val event = _event.asSharedFlow()

    private var saveChangesJob: Job? = null
    private var loadMeJob: Job? = null
    private var observeProfileJob: Job? = null

    fun onAction(action: Profile.Action) = viewModelScope.launch {
        when (action) {
            Profile.Action.OnBack -> _event.emit(OnBack)
            is Profile.Action.OnNavigate -> _event.emit(OnNavigate(route = action.route))

            is Profile.Action.OnAvatarChange -> {
                _state.update { s ->
                    s.copy(
                        profile = state.value.profile?.copy(
                            avatarUrl = action.avatarUrl
                        ),
                        showChangeAvatarDialog = false
                    )
                }

                saveChanges(
                    avatarUrl = action.avatarUrl
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
                    profile = s.profile?.copy(
                        email = action.email
                    )
                )
            }

            is Profile.Action.OnNameChange -> _state.update { s ->
                s.copy(
                    profile = s.profile?.copy(
                        fullName = action.name
                    )

                )
            }

            Profile.Action.OnCloseTimeChange -> _state.update { s ->
                s.copy(
                    closedTimeNotifications = !state.value.closedTimeNotifications
                )
            }

            Profile.Action.OnNearbyDealsChange -> _state.update { s ->
                s.copy(
                    nearbyDealsNotifications = !state.value.nearbyDealsNotifications
                )
            }

            Profile.Action.OnSave -> saveChanges(
                name = state.value.profile?.fullName,
                email = state.value.profile?.email,
                notifyNearby = state.value.profile?.notifyNearby,
                notifyClosingSoon = state.value.profile?.notifyClosingSoon,
                avatarUrl = state.value.profile?.avatarUrl
            )

            Profile.Action.OnUnlockPremium -> {
                _event.emit(Profile.Event.OnNavigate(route = Screen.Premium))
            }
        }
    }

    private fun saveChanges(
        name: String? = null,
        email: String? = null,
        notifyNearby: Boolean? = null,
        notifyClosingSoon: Boolean? = null,
        avatarUrl: String? = null
    ) {
        if (name?.isEmpty() == true) {
            _state.update { s ->
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
                email = email,
                notifyNearby = notifyNearby,
                notifyClosingSoon = notifyClosingSoon,
                avatarUrl = avatarUrl
            ).onSuccess {}
        }
    }

    private fun loadMe() {
        loadMeJob?.cancel()
        loadMeJob = viewModelScope.launch {
            profileRepository.loadMe()
        }
    }

    private fun observeProfile() {
        observeProfileJob?.cancel()
        observeProfileJob = viewModelScope.launch(Dispatchers.IO) {
            profileRepository.getMe().collect { profile ->
                _state.update { s ->
                    s.copy(
                        profile = profile,
                    )
                }
            }
        }
    }
}