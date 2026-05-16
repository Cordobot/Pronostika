package cordobot.example.pronstika.domain.repository

import cordobot.example.pronstika.domain.model.WeatherModel

interface WeatherRepository {
    suspend fun getWeather(city: String): Result<WeatherModel>
}
