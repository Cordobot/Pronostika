package cordobot.example.pronstika.presentation.viewmodel

import cordobot.example.pronstika.domain.model.WeatherForecast
import cordobot.example.pronstika.domain.model.WeatherModel
import cordobot.example.pronstika.domain.repository.RecentCitiesRepository
import cordobot.example.pronstika.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private class FakeWeatherRepository : WeatherRepository {
        var result: Result<WeatherForecast> = Result.failure(Exception("Ciudad no encontrada"))
        val requested = mutableListOf<String>()

        override suspend fun getWeather(city: String): Result<WeatherForecast> {
            requested += city
            return result
        }
    }

    private class FakeRecentCities(initial: List<String> = emptyList()) : RecentCitiesRepository {
        private var cities = initial
        override fun getRecentCities() = cities
        override fun addCity(city: String) = (listOf(city) + cities.filterNot { it == city }).also { cities = it }
        override fun removeCity(city: String) = cities.filterNot { it == city }.also { cities = it }
    }

    private fun forecastFor(city: String) = WeatherForecast(
        current = WeatherModel(
            city = city, country = "CO", temperature = 20.0, feelsLike = 19.0, humidity = 60,
            pressure = 1012, description = "nubes", iconCode = "04d", windSpeed = 3.0,
            tempMin = 15.0, tempMax = 22.0, sunrise = 0, sunset = 0, timezoneOffset = -18_000
        ),
        hourly = emptyList(),
        daily = emptyList()
    )

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `successful search shows the forecast and saves the city`() {
        val repository = FakeWeatherRepository().apply { result = Result.success(forecastFor("Bogotá")) }
        val viewModel = WeatherViewModel(repository, FakeRecentCities())

        viewModel.fetchWeather("  bogota ")

        assertEquals(listOf("bogota"), repository.requested)
        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
        assertEquals(listOf("Bogotá"), viewModel.recent.value)
    }

    @Test
    fun `failed search shows the error and retry repeats the last query`() {
        val repository = FakeWeatherRepository()
        val viewModel = WeatherViewModel(repository, FakeRecentCities())

        viewModel.fetchWeather("Atlantis")
        assertEquals(WeatherUiState.Error("Ciudad no encontrada"), viewModel.uiState.value)

        repository.result = Result.success(forecastFor("Atlantis"))
        viewModel.retry()

        assertEquals(listOf("Atlantis", "Atlantis"), repository.requested)
        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
    }

    @Test
    fun `blank search is ignored`() {
        val repository = FakeWeatherRepository()
        val viewModel = WeatherViewModel(repository, FakeRecentCities())

        viewModel.fetchWeather("   ")

        assertTrue(repository.requested.isEmpty())
        assertEquals(WeatherUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `opens with the last searched city`() {
        val repository = FakeWeatherRepository().apply { result = Result.success(forecastFor("Medellín")) }

        WeatherViewModel(repository, FakeRecentCities(listOf("Medellín", "Cali")))

        assertEquals(listOf("Medellín"), repository.requested)
    }
}
