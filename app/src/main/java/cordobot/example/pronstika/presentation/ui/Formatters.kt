package cordobot.example.pronstika.presentation.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

private val spanish = Locale("es", "ES")

/** Formats an epoch time (seconds) in the local time of the city, given its UTC offset. */
private fun format(pattern: String, epochSeconds: Long, timezoneOffset: Int): String =
    SimpleDateFormat(pattern, spanish).apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date((epochSeconds + timezoneOffset) * 1000))

fun formatHour(epochSeconds: Long, timezoneOffset: Int): String =
    format("HH:mm", epochSeconds, timezoneOffset)

fun formatDay(epochSeconds: Long, timezoneOffset: Int, isToday: Boolean): String =
    if (isToday) "Hoy"
    else format("EEE d", epochSeconds, timezoneOffset).replaceFirstChar { it.uppercase() }

fun Double.toDegrees(): String = "${roundToInt()}°"

fun Double.toPercent(): String = "${(this * 100).roundToInt()}%"

fun weatherIconUrl(iconCode: String, size: String = "2x"): String =
    "https://openweathermap.org/img/wn/$iconCode@$size.png"
