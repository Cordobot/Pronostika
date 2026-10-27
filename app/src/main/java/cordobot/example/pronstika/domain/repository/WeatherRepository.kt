package cordobot.example.pronstika.domain.repository

import cordobot.example.pronstika.domain.model.WeatherForecast

interface WeatherRepository {
    suspend fun getWeather(city: String): Result<WeatherForecast>
}
