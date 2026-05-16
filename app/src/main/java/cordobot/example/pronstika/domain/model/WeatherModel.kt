package cordobot.example.pronstika.domain.model

data class WeatherModel(
    val city: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val description: String,
    val iconCode: String,
    val windSpeed: Double,
    val tempMin: Double,
    val tempMax: Double
)
