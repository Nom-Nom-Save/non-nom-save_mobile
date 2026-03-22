package ua.nure.nomnomsave.repository.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

