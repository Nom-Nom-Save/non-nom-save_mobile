package ua.nure.nomnomsave.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NestedGraph {
    @Serializable data object Profile : NestedGraph()
    @Serializable data object Cart : NestedGraph()
    @Serializable data object ListView : NestedGraph()
    @Serializable data object Favorite : NestedGraph()
}