package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class EstablishmentDetailDto(
    val id: String,
    val email: String? = null,
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val workingHours: String? = null,
    val logo: String? = null,
    val banner: String? = null,
    val rating: String? = null,
    val createdAt: String? = null,
    val isEmailVerified: Boolean = false,
    val status: String? = null,
)

@Serializable
data class EstablishmentDetailPrivateDto(
    val id: String,
    val email: String? = null,
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val workingHours: String? = null,
    val logo: String? = null,
    val banner: String? = null,
    val rating: String? = null,
    val createdAt: String? = null,
    val isEmailVerified: Boolean = false,
    val status: String? = null,
    val boundTo: String? = null,
)

@Serializable
data class UpdateEstablishmentInput(
    val name: String? = null,
    val email: String? = null,
    val description: String? = null,
    val address: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val workingHours: String? = null,
    val logo: String? = null,
    val banner: String? = null,
    val boundTo: String? = null,
)


