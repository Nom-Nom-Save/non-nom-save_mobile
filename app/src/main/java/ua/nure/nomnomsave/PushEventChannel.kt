package ua.nure.nomnomsave

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object PushEventChannel {
    private val channel = Channel<PushEvent>(capacity = Channel.BUFFERED)

    suspend fun send(event: PushEvent) {
        channel.send(event)
    }

    fun receiveFlow(): Flow<PushEvent> = channel.receiveAsFlow()
}