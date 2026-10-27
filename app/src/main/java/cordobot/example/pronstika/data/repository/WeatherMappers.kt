package cordobot.example.pronstika.data.repository

import cordobot.example.pronstika.data.remote.ForecastItem
import cordobot.example.pronstika.data.remote.WeatherResponse
import cordobot.example.pronstika.domain.model.DailyForecast
import cordobot.example.pronstika.domain.model.HourlyForecast
import cordobot.example.pronstika.domain.model.WeatherModel
import kotlin.math.abs

private const val SECONDS_PER_DAY = 86_400L
private const val SECONDS_PER_HOUR = 3_600L
private const val HOURLY_ENTRIES = 8 // 8 x 3 h = next 24 hours
private const val DAILY_ENTRIES = 5

fun WeatherResponse.toDomain() = WeatherModel(
    city = name,
    country = sys.country,
    temperature = main.temp,
    feelsLike = main.feels_like,
    humidity = main.humidity,
    pressure = main.pressure,
    description = weather.firstOrNull()?.description ?: "",
    iconCode = weather.firstOrNull()?.icon ?: "",
    windSpeed = wind.speed,
    tempMin = main.temp_min,
    tempMax = main.temp_max,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
    timezoneOffset = timezone
)

fun List<ForecastItem>.toHourly(): List<HourlyForecast> =
    take(HOURLY_ENTRIES).map {
        HourlyForecast(
            time = it.dt,
            temperature = it.main.temp,
            iconCode = it.weather.firstOrNull()?.icon ?: "",
            precipitationChance = it.pop
        )
    }

/**
 * Groups the 3-hour entries by local day (using the UTC offset of the city) and keeps, per day,
 * the lowest/highest temperature, the highest chance of rain and the icon closest to midday.
 */
fun List<ForecastItem>.toDaily(timezoneOffset: Int): List<DailyForecast> =
    groupBy { (it.dt + timezoneOffset) / SECONDS_PER_DAY }
        .toSortedMap()
        .values
        .take(DAILY_ENTRIES)
        .map { items ->
            val midday = items.minBy { abs(localHour(it.dt, timezoneOffset) - 12) }
            DailyForecast(
                date = items.first().dt,
                tempMin = items.minOf { it.main.temp_min },
                tempMax = items.maxOf { it.main.temp_max },
                iconCode = midday.weather.firstOrNull()?.icon ?: "",
                description = midday.weather.firstOrNull()?.description ?: "",
                precipitationChance = items.maxOf { it.pop }
            )
        }

private fun localHour(epochSeconds: Long, timezoneOffset: Int): Long =
    ((epochSeconds + timezoneOffset) % SECONDS_PER_DAY) / SECONDS_PER_HOUR
