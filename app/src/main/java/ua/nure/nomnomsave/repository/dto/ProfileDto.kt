package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String? = null,
    val isEmailVerified: Boolean,
    val createdAt: String,
)
