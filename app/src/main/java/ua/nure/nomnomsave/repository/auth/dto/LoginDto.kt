package ua.nure.nomnomsave.repository.auth.dto

import kotlinx.serialization.Serializable

@Serializable
class LoginDto (
    val message: String,
    val accessToken: String,
)