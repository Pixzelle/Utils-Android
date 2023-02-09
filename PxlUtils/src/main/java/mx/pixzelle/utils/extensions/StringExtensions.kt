package mx.pixzelle.utils.extensions

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun String.toLocalDate(pattern: String) =
    LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))

fun String.toLocalDateTime(pattern: String) =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))

fun String.toZonedDateTime(pattern: String) =
    ZonedDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))

fun String.isInt() = try {
    this.toInt()
    true
}catch(ex : Exception) {
    false
}