package cordobot.example.pronstika.data.repository

import cordobot.example.pronstika.data.remote.WeatherApiService
import cordobot.example.pronstika.data.remote.WeatherResponse
import cordobot.example.pronstika.domain.model.WeatherModel
import cordobot.example.pronstika.domain.repository.WeatherRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService
) : WeatherRepository {

    override suspend fun getWeather(city: String): Result<WeatherModel> {
        return try {
            val response = api.getCurrentWeather(city)
            Result.success(response.toDomain())
        } catch (e: HttpException) {
            val message = when (e.code()) {
                401 -> "API key inválida"
                404 -> "Ciudad no encontrada"
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

// Mapper extension function
fun WeatherResponse.toDomain() = WeatherModel(
    city = name,
    country = sys.country,
    temperature = main.temp,
    feelsLike = main.feels_like,
    humidity = main.humidity,
    description = weather.firstOrNull()?.description ?: "",
    iconCode = weather.firstOrNull()?.icon ?: "",
    windSpeed = wind.speed,
    tempMin = main.temp_min,
    tempMax = main.temp_max
)
