package ua.nure.nomnomsave.repository.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductTypeDto(
    val id: String,
    val name: String,
)

@Serializable
data class ProductTypesResponse(
    val productTypes: List<ProductTypeDto>,
)


