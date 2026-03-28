package ua.nure.nomnomsave.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.nure.nomnomsave.db.data.DbConverters
import ua.nure.nomnomsave.db.data.dao.OrderDao
import ua.nure.nomnomsave.db.data.dao.OrderDetailsDao
import ua.nure.nomnomsave.db.data.dao.ProfileDao
import ua.nure.nomnomsave.db.data.entity.OrderDetailsEntity
import ua.nure.nomnomsave.db.data.entity.OrderEntity
import ua.nure.nomnomsave.db.data.entity.ProfileEntity

@Database(
    entities = [
        ProfileEntity::class,
        OrderEntity::class,
        OrderDetailsEntity::class
               ],
    version = 2
)

@TypeConverters(DbConverters::class)
abstract class AppDb : RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val orderDao: OrderDao
    abstract val orderDetailsDao: OrderDetailsDao
}