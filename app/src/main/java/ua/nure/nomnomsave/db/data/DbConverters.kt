package ua.nure.nomnomsave.db.data

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import ua.nure.nomnomsave.repository.dto.OrderStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class DbConverters {
    @TypeConverter
    fun localDateTimeToDb(value: LocalDateTime?): Long? {
        return value
            ?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
    }

    @TypeConverter
    fun localDateTimeFromDb(value: Long?): LocalDateTime? {
        return value
            ?.let { Instant.ofEpochMilli(it) }
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDateTime()
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString(it) }
    }

    @TypeConverter
    fun orderStatusToDb(value: OrderStatus?): String? {
        return value?.name
    }

    @TypeConverter
    fun orderStatusFromDb(value: String?): OrderStatus? {
        return value?.let { OrderStatus.valueOf(it) }
    }

}