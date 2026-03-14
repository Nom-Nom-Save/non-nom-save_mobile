package ua.nure.nomnomsave.db

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ua.nure.nomnomsave.db.data.AppDb
import ua.nure.nomnomsave.repository.token.TokenRepository

class DbRepositoryImpl(
    private val context: Context,
    private val tokenRepository: TokenRepository
): DbRepository {

    override val db: AppDb
        get() = checkNotNull(_db) {"Db must be initializes"}
    override val dbFlow: Flow<AppDb>
        get() = _dbFlow.asStateFlow().filterNotNull()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            tokenRepository.userNameFlow.distinctUntilChanged { old, new -> old == new }.collect { profile ->
                if(profile == null) {
                    synchronized(this) {
                        _db = null
                    }
                    _dbFlow.emit(null)
                } else {
                    synchronized(this) {
                        _db = createDb(profile)
                    }
                    _dbFlow.emit(_db)
                }
            }
        }
    }

    private fun createDb(dbName: String): AppDb {
        return Room.databaseBuilder(
            context,
            AppDb::class.java,
            name = dbName
        )
            .fallbackToDestructiveMigration()
            .build()
    }


    companion object {
        private val _dbFlow: MutableStateFlow<AppDb?> = MutableStateFlow(null)
        private var _db: AppDb? = null
    }
}