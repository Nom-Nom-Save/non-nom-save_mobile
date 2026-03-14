package ua.nure.nomnomsave.repository.token

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.config.PreferencesKeys

class TokenRepositoryImpl (
    private val dataStore: DataStore<Preferences>
) : TokenRepository {
    override val token
        get() = _token
    override val userName: String?
        get() = _userName
    private val _userNameFlow = MutableStateFlow<UserName>(null)
    override val userNameFlow: StateFlow<UserName>
        get() = _userNameFlow.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _token = dataStore.data.map {
                it[PreferencesKeys.token]
            }.firstOrNull()
        }
    }

    override suspend fun setToken(newToken: String?): Unit = withContext(Dispatchers.IO) {
        _token = newToken
        if (newToken == null) {
            dataStore.edit {
                it.remove(PreferencesKeys.token)
            }
        } else {
            dataStore.edit {
                it[PreferencesKeys.token] = newToken
            }
        }
    }

    override suspend fun setUserName(newUserName: String?) {
        _userName = newUserName
        _userNameFlow.emit(newUserName)
    }

    companion object {
        private var _token: String? = null
        private var _userName: String? = null
    }
}