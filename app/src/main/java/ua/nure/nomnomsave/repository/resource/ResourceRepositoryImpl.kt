package ua.nure.nomnomsave.repository.resource

import android.content.Context

class ResourceRepositoryImpl (
    private val context: Context,

): ResourceRepository {
    override fun getStringByResource(res: Int): String {
        return context.getString(res)
    }
}