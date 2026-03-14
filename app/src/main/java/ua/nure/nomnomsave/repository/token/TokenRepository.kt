package ua.nure.nomnomsave.repository.token

import kotlinx.coroutines.flow.StateFlow

interface TokenRepository {
    val token: String?
    val userName: String?
    val userNameFlow: StateFlow<UserName>
    suspend fun setToken(newToken: String?)
    suspend fun setUserName(newUserName: String?)
}

typealias UserName = String?