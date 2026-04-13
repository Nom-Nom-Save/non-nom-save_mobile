package ua.nure.nomnomsave.repository.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLogInRequest(
    val token: String,
    val email: String? = null,
)
