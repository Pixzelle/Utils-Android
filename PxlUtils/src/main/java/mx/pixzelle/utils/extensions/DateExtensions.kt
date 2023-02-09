package mx.pixzelle.utils.extensions

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.format(pattern : String = "dd/MM/yyyy") : String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalDateTime.format(pattern : String = "dd/MM/yyyy HH:mm:ss") : String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun ZonedDateTime.format(pattern : String = "dd/MM/yyyy'T'HH:mm:ssZ") : String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalTime.format(pattern : String = "HH:mm") : String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}