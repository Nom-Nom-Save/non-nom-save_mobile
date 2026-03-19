package ua.nure.nomnomsave.repository.auth.dto

import kotlinx.serialization.Serializable

@Serializable
class LoginRequest (
    val email: String,
    val password: String,
    val loginType: String
)