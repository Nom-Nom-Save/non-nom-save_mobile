package ua.nure.nomnomsave.repository.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val newPassword: String
)

