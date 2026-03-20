package ua.nure.nomnomsave.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NestedGraph {
    @Serializable data object Profile : NestedGraph()
//    @Serializable data object Trainer : NestedGraph()
//    @Serializable data object Chat : NestedGraph()
//    @Serializable data object Analytics : NestedGraph()
//    @Serializable data object OwnTrainer: NestedGraph()
}