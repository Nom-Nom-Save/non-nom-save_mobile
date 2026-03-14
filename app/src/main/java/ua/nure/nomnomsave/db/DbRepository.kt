package ua.nure.nomnomsave.db

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.AppDb

interface DbRepository {
    val dbFlow: Flow<AppDb>
    val db: AppDb
}