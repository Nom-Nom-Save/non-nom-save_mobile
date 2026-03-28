package ua.nure.nomnomsave.extention

import java.time.Instant
import java.time.ZoneId

fun String?.toLocalDateTime() = Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDateTime()