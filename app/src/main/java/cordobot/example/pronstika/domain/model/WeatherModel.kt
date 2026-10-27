package cordobot.example.pronstika.domain.model

data class WeatherModel(
    val city: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val description: String,
    val iconCode: String,
    val windSpeed: Double,
    val tempMin: Double,
    val tempMax: Double,
    // Epoch seconds (UTC)
    val sunrise: Long,
    val sunset: Long,
    // Shift in seconds from UTC of the city
    val timezoneOffset: Int
)

data class HourlyForecast(
    val time: Long,
    val temperature: Double,
    val iconCode: String,
    val precipitationChance: Double
)

data class DailyForecast(
    val date: Long,
    val tempMin: Double,
    val tempMax: Double,
    val iconCode: String,
    val description: String,
    val precipitationChance: Double
)

data class WeatherForecast(
    val current: WeatherModel,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)
