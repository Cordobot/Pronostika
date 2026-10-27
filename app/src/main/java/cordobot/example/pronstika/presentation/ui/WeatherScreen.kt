package cordobot.example.pronstika.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import cordobot.example.pronstika.domain.model.DailyForecast
import cordobot.example.pronstika.domain.model.HourlyForecast
import cordobot.example.pronstika.domain.model.WeatherForecast
import cordobot.example.pronstika.domain.model.WeatherModel
import cordobot.example.pronstika.presentation.viewmodel.WeatherUiState
import cordobot.example.pronstika.presentation.viewmodel.WeatherViewModel

private val GlassColor = Color.White.copy(alpha = 0.12f)
private val SecondaryText = Color.White.copy(alpha = 0.7f)

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recent by viewModel.recent.collectAsStateWithLifecycle()
    var cityInput by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val search: (String) -> Unit = { city ->
        viewModel.fetchWeather(city)
        keyboardController?.hide()
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027),
            Color(0xFF203A43),
            Color(0xFF2C5364)
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .systemBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Header(
                showRefresh = uiState is WeatherUiState.Success,
                onRefresh = viewModel::retry
            )
        }

        item {
            SearchField(
                value = cityInput,
                onValueChange = { cityInput = it },
                onSearch = { search(cityInput) }
            )
        }

        if (recent.isNotEmpty()) {
            item {
                RecentCities(
                    cities = recent,
                    onSelect = search,
                    onRemove = viewModel::removeRecent
                )
            }
        }

        item {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "WeatherContentTransition"
            ) { state ->
                when (state) {
                    is WeatherUiState.Idle -> CenteredMessage("Ingresa una ciudad para ver su pronóstico")
                    is WeatherUiState.Loading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp)
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    is WeatherUiState.Success -> ForecastContent(state.forecast)
                    is WeatherUiState.Error -> ErrorMessage(state.message, onRetry = viewModel::retry)
                }
            }
        }
    }
}

@Composable
private fun Header(showRefresh: Boolean, onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Pronóstika",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
        )
        if (showRefresh) {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
            }
        }
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit, onSearch: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Buscar ciudad...", color = Color.White.copy(alpha = 0.6f)) },
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
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
}

@Composable
private fun RecentCities(cities: List<String>, onSelect: (String) -> Unit, onRemove: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(cities, key = { it }) { city ->
            AssistChip(
                onClick = { onSelect(city) },
                label = { Text(city) },
                // Tapping the chip searches the city; tapping the x removes it from the list
                trailingIcon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Quitar $city",
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onRemove(city) }
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = GlassColor,
                    labelColor = Color.White,
                    trailingIconContentColor = SecondaryText
                ),
                border = null
            )
        }
    }
}

@Composable
private fun ForecastContent(forecast: WeatherForecast) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CurrentWeatherCard(forecast.current)
        DetailsGrid(forecast.current)
        if (forecast.hourly.isNotEmpty()) {
            SectionTitle("Próximas 24 horas")
            HourlyRow(forecast.hourly, forecast.current.timezoneOffset)
        }
        if (forecast.daily.isNotEmpty()) {
            SectionTitle("Próximos 5 días")
            DailyList(forecast.daily, forecast.current.timezoneOffset)
        }
        Text(
            text = "Datos: OpenWeather",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CurrentWeatherCard(weather: WeatherModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weather.city}, ${weather.country}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            AsyncImage(
                model = weatherIconUrl(weather.iconCode, "4x"),
                contentDescription = weather.description,
                modifier = Modifier.size(130.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = weather.temperature.toDegrees(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 76.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Máx ${weather.tempMax.toDegrees()} · Mín ${weather.tempMin.toDegrees()}",
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun DetailsGrid(weather: WeatherModel) {
    val details = listOf(
        "💧 Humedad" to "${weather.humidity}%",
        "🌬️ Viento" to "${weather.windSpeed} m/s",
        "🌡️ Sensación" to weather.feelsLike.toDegrees(),
        "🧭 Presión" to "${weather.pressure} hPa",
        "🌅 Amanecer" to formatHour(weather.sunrise, weather.timezoneOffset),
        "🌇 Atardecer" to formatHour(weather.sunset, weather.timezoneOffset)
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        details.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (label, value) ->
                    WeatherDetailItem(label, value, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(GlassColor, RoundedCornerShape(20.dp))
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun HourlyRow(hours: List<HourlyForecast>, timezoneOffset: Int) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(hours, key = { it.time }) { hour ->
            Column(
                modifier = Modifier
                    .background(GlassColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(formatHour(hour.time, timezoneOffset), color = SecondaryText, style = MaterialTheme.typography.labelMedium)
                AsyncImage(
                    model = weatherIconUrl(hour.iconCode),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp)
                )
                Text(hour.temperature.toDegrees(), color = Color.White, fontWeight = FontWeight.Bold)
                Text("☔ ${hour.precipitationChance.toPercent()}", color = SecondaryText, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun DailyList(days: List<DailyForecast>, timezoneOffset: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassColor, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        days.forEachIndexed { index, day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDay(day.date, timezoneOffset, isToday = index == 0),
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(72.dp)
                )
                AsyncImage(
                    model = weatherIconUrl(day.iconCode),
                    contentDescription = day.description,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "☔ ${day.precipitationChance.toPercent()}",
                    color = SecondaryText,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(day.tempMin.toDegrees(), color = SecondaryText)
                Spacer(Modifier.width(12.dp))
                Text(day.tempMax.toDegrees(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            if (index < days.lastIndex) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Text(
        text = text,
        color = SecondaryText,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp)
    )
}

@Composable
private fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = Color(0xFFFFB4AB),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = GlassColor, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Reintentar")
        }
    }
}
