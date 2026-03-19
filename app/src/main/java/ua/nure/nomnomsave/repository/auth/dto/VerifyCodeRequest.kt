package ua.nure.nomnomsave.repository.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifyCodeRequest(
    val email: String,
    val code: String
)