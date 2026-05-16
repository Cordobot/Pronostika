package cordobot.example.pronstika.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import cordobot.example.pronstika.domain.model.WeatherModel
import cordobot.example.pronstika.presentation.viewmodel.WeatherUiState
import cordobot.example.pronstika.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var cityInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            
            Text(
                text = "Pronstika",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            )
            
            Spacer(Modifier.height(32.dp))

            // Search Bar with Premium Style
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                placeholder = { Text("Buscar ciudad...", color = Color.White.copy(alpha = 0.6f)) },
                trailingIcon = {
                    IconButton(onClick = { 
                        viewModel.fetchWeather(cityInput)
                        keyboardController?.hide()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { 
                        viewModel.fetchWeather(cityInput)
                        keyboardController?.hide()
                    }
                ),
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))

            // State Handling with Animated Visibility
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "WeatherContentTransition"
            ) { state ->
                when (state) {
                    is WeatherUiState.Idle -> {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                "Ingresa una ciudad para comenzar",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    is WeatherUiState.Loading -> {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    is WeatherUiState.Success -> {
                        WeatherCard(weather = state.weather)
                    }
                    is WeatherUiState.Error -> {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(weather: WeatherModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weather.city}, ${weather.country}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(Modifier.height(16.dp))

            // Weather Icon using Coil
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${weather.iconCode}@4x.png",
                contentDescription = weather.description,
                modifier = Modifier.size(140.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "${weather.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem("💧 Humedad", "${weather.humidity}%")
                WeatherDetailItem("🌬️ Viento", "${weather.windSpeed} m/s")
                WeatherDetailItem("🌡️ Sensación", "${weather.feelsLike.toInt()}°C")
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
