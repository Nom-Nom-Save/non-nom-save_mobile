package ua.nure.nomnomsave.repository.profile.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val fullName: String? = null,
    val email: String? = null,
    val avatarUrl: String? = null,
)
