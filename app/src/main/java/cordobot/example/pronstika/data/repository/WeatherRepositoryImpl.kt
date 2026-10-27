package cordobot.example.pronstika.data.repository

import cordobot.example.pronstika.data.remote.WeatherApiService
import cordobot.example.pronstika.domain.model.WeatherForecast
import cordobot.example.pronstika.domain.repository.WeatherRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getWeather(city: String): Result<WeatherForecast> {
        return try {
            // Current conditions and forecast are requested in parallel
            val forecast = coroutineScope {
                val current = async { api.getCurrentWeather(city) }
                val upcoming = async { api.getForecast(city) }
                val forecastResponse = upcoming.await()
                WeatherForecast(
                    current = current.await().toDomain(),
                    hourly = forecastResponse.list.toHourly(),
                    daily = forecastResponse.list.toDaily(forecastResponse.city.timezone)
                )
            }
            Result.success(forecast)
        } catch (e: HttpException) {
            val message = when (e.code()) {
                401 -> "API key inválida"
                404 -> "Ciudad no encontrada"
                429 -> "Demasiadas consultas, intenta en un momento"
                else -> "Error del servidor: ${e.code()}"
            }
            Result.failure(Exception(message))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión a internet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
