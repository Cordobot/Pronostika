package cordobot.example.pronstika.data.remote

data class WeatherResponse(
    val name: String,
    val dt: Long,
    val timezone: Int,
    val main: Main,
    val weather: List<WeatherDescription>,
    val wind: Wind,
    val sys: Sys
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int,
    val temp_min: Double,
    val temp_max: Double
)

data class WeatherDescription(
    val description: String,
    val icon: String
)

data class Wind(val speed: Double)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
