package ua.nure.nomnomsave.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ua.nure.nomnomsave.db.dao.MenuDao
import ua.nure.nomnomsave.db.data.dao.CartItemDao
import ua.nure.nomnomsave.db.data.dao.EstablishedDao
import ua.nure.nomnomsave.db.data.dao.FavoriteDao
import ua.nure.nomnomsave.db.data.dao.OrderDao
import ua.nure.nomnomsave.db.data.dao.OrderDetailsDao
import ua.nure.nomnomsave.db.data.dao.ProfileDao
import ua.nure.nomnomsave.db.data.dao.ReviewDao
import ua.nure.nomnomsave.db.data.entity.CartItemEntity
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.entity.FavoriteEntity
import ua.nure.nomnomsave.db.data.entity.OrderDetailsEntity
import ua.nure.nomnomsave.db.data.entity.OrderEntity
import ua.nure.nomnomsave.db.data.entity.ProfileEntity
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.db.data.entity.ReviewEntity

@Database(
    entities = [
        ProfileEntity::class,
        OrderEntity::class,
        OrderDetailsEntity::class,
        CartItemEntity::class,
        FavoriteEntity::class,
        EstablishmentEntity::class,
        MenuEntity::class,
        ReviewEntity::class
    ],
    version = 9
)

@TypeConverters(DbConverters::class)
abstract class AppDb : RoomDatabase() {
    abstract val profileDao: ProfileDao
    abstract val orderDao: OrderDao
    abstract val orderDetailsDao: OrderDetailsDao
    abstract val cartItemDao: CartItemDao
    abstract val favoriteDao: FavoriteDao
    abstract val establishedDao: EstablishedDao
    abstract val menuDao: MenuDao
    abstract val reviewDao: ReviewDao
}