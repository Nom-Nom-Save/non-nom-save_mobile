package ua.nure.nomnomsave.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.nure.nomnomsave.db.data.DbConverters
import ua.nure.nomnomsave.db.data.dao.ProfileDao
import ua.nure.nomnomsave.db.data.entity.ProfileEntity

@Database(
    entities = [
        ProfileEntity::class
               ],
    version = 1
)

@TypeConverters(DbConverters::class)
abstract class AppDb : RoomDatabase() {
    abstract val profileDao: ProfileDao
}