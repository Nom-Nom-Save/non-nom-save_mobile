package ua.nure.nomnomsave.repository.resource

interface ResourceRepository {
    fun getStringByResource(res: Int): String
}