package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDataDto(
    val message: String,
    val user: ProfileDto
)

@Serializable
data class ProfileDto(
    val id: String,
    val fullName: String,
    val email: String,
    val avatarUrl: String? = null,
    val isEmailVerified: Boolean,
    val notifyNearby: Boolean = false,
    val notifyClosingSoon: Boolean = false,
    val notifyNewItems: Boolean = false,
    val createdAt: String,
)
