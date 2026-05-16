package cordobot.example.pronstika.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cordobot.example.pronstika.domain.model.WeatherModel
import cordobot.example.pronstika.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun fetchWeather(city: String) {
        if (city.isBlank()) return

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            repository.getWeather(city)
                .onSuccess { weather ->
                    _uiState.value = WeatherUiState.Success(weather)
                }
                .onFailure { error ->
                    _uiState.value = WeatherUiState.Error(error.message ?: "Error desconocido")
                }
        }
    }
}

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val weather: WeatherModel) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
