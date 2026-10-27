package cordobot.example.pronstika.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cordobot.example.pronstika.domain.model.WeatherForecast
import cordobot.example.pronstika.domain.repository.RecentCitiesRepository
import cordobot.example.pronstika.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val recentCities: RecentCitiesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _recent = MutableStateFlow(recentCities.getRecentCities())
    val recent: StateFlow<List<String>> = _recent.asStateFlow()

    private var lastQuery: String? = null
    private var fetchJob: Job? = null

    init {
        // Opens with the weather of the last searched city
        _recent.value.firstOrNull()?.let(::fetchWeather)
    }

    fun fetchWeather(city: String) {
        val query = city.trim()
        if (query.isBlank()) return
        lastQuery = query

        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            repository.getWeather(query)
                .onSuccess { forecast ->
                    _uiState.value = WeatherUiState.Success(forecast)
                    // The name returned by the API is stored, so "bogota" is saved as "Bogotá"
                    _recent.value = recentCities.addCity(forecast.current.city)
                }
                .onFailure { error ->
                    _uiState.value = WeatherUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }

    fun retry() {
        lastQuery?.let(::fetchWeather)
    }

    fun removeRecent(city: String) {
        _recent.value = recentCities.removeCity(city)
    }
}

sealed class WeatherUiState {
    data object Idle : WeatherUiState()
    data object Loading : WeatherUiState()
    data class Success(val forecast: WeatherForecast) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
