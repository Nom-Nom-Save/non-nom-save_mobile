package ua.nure.nomnomsave.repository

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val success: Boolean,
    val message: String,
    @SerialName("request_id") val requestId: String,
    val timestamp: String,
    val code: String,
    val status: Int,
    val errors: List<AttributeError>? = null

)

@Serializable
data class AttributeError(
    val attribute: String,
    val message: String,
)