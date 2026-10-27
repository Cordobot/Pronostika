package cordobot.example.pronstika.data.remote

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: ForecastCity
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherDescription>,
    // Probability of precipitation, 0..1
    val pop: Double = 0.0
)

data class ForecastCity(
    val name: String,
    val country: String,
    val timezone: Int
)
