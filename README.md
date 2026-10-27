# Pronóstika 🌤️

Aplicación Android nativa del clima: busca cualquier ciudad y muestra las condiciones actuales,
el pronóstico de las próximas 24 horas y de los próximos 5 días. Construida con **Kotlin**,
**Jetpack Compose** y **Clean Architecture** sobre la API de [OpenWeather](https://openweathermap.org/api).

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Hilt](https://img.shields.io/badge/DI-Hilt-34A853)
![minSdk](https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white)

---

## Tabla de contenido

- [Funcionalidades](#funcionalidades)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Compilar y ejecutar](#compilar-y-ejecutar)
- [Pruebas](#pruebas)
- [Roadmap](#roadmap)

## Funcionalidades

- 🔎 **Búsqueda por ciudad** con teclado de acción "Buscar".
- 🌡️ **Clima actual**: temperatura, sensación térmica, máx/mín, humedad, viento, presión,
  amanecer y atardecer (en la hora local de la ciudad consultada).
- ⏱️ **Próximas 24 horas** en intervalos de 3 h con probabilidad de lluvia.
- 📅 **Pronóstico de 5 días** agrupado por día local (mín/máx, icono representativo del mediodía y
  máxima probabilidad de lluvia).
- 🕘 **Búsquedas recientes**: las últimas 5 ciudades se guardan y la app abre con la última consultada.
- 🔁 **Actualizar / Reintentar** y mensajes de error claros (ciudad no encontrada, sin conexión,
  API key inválida, límite de consultas).
- ✨ UI con degradado y tarjetas "glass", transiciones animadas entre estados.

## Stack tecnológico

| Área | Tecnología |
|------|------------|
| Lenguaje | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | Clean Architecture + MVVM, `StateFlow` |
| Inyección de dependencias | Hilt (KSP) |
| Red | Retrofit 2 + Gson + OkHttp (logging solo en debug) |
| Asincronía | Kotlin Coroutines (peticiones en paralelo con `async`) |
| Imágenes | Coil |
| Persistencia ligera | SharedPreferences (ciudades recientes) |
| Pruebas | JUnit 4 + kotlinx-coroutines-test |

## Arquitectura

```
┌───────────────────────── presentation ─────────────────────────┐
│ WeatherScreen (Compose)  ◄── StateFlow ──  WeatherViewModel    │
└───────────────────────────────┬────────────────────────────────┘
                                │ interfaces
┌──────────────────────────── domain ────────────────────────────┐
│ WeatherForecast · WeatherModel · Hourly/DailyForecast          │
│ WeatherRepository · RecentCitiesRepository                     │
└───────────────────────────────┬────────────────────────────────┘
                                │ implementaciones
┌───────────────────────────── data ─────────────────────────────┐
│ WeatherRepositoryImpl ─► WeatherApiService (Retrofit)          │
│ WeatherMappers (DTO → dominio, agrupación diaria)              │
│ RecentCitiesStorage (SharedPreferences)                        │
└────────────────────────────────────────────────────────────────┘
```

- La UI solo observa un `WeatherUiState` sellado: `Idle`, `Loading`, `Success`, `Error`.
- El repositorio pide `/weather` y `/forecast` **en paralelo** y traduce los errores HTTP a mensajes
  para el usuario.
- La agrupación del pronóstico por día usa el desfase horario de la ciudad, no el del teléfono.

## Estructura del proyecto

```
app/src/main/java/cordobot/example/pronstika/
├── data/
│   ├── local/        RecentCitiesStorage.kt
│   ├── remote/       WeatherApiService.kt, WeatherResponse.kt, ForecastResponse.kt
│   └── repository/   WeatherRepositoryImpl.kt, WeatherMappers.kt
├── di/               NetworkModule.kt, RepositoryModule.kt
├── domain/
│   ├── model/        WeatherModel.kt
│   └── repository/   WeatherRepository.kt, RecentCitiesRepository.kt
├── presentation/
│   ├── ui/           WeatherScreen.kt, Formatters.kt
│   └── viewmodel/    WeatherViewModel.kt
└── ui/theme/         Tema Material 3
```

## Configuración

1. Crea una cuenta gratuita en [OpenWeather](https://home.openweathermap.org/users/sign_up) y copia tu API key.
2. Agrégala en `local.properties` (este archivo **no** se versiona):

   ```properties
   WEATHER_API_KEY=tu_api_key
   ```

   También puedes pasarla al compilar: `./gradlew assembleDebug -PWEATHER_API_KEY=tu_api_key`.

## Compilar y ejecutar

Requisitos: Android Studio (Ladybug o superior), JDK 17+, Android SDK 35.

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

O abre el proyecto en Android Studio y ejecuta la configuración **app**.

## Pruebas

```bash
./gradlew :app:testDebugUnitTest
```

- `WeatherMappersTest`: agrupación por día local, mín/máx, probabilidad de lluvia y límites (24 h / 5 días).
- `WeatherViewModelTest`: búsqueda exitosa, error + reintento, búsqueda vacía y carga inicial de la última ciudad.

## Roadmap

- [ ] Ubicación actual del dispositivo (GPS).
- [ ] Caché offline con Room.
- [ ] Unidades configurables (°C / °F).
- [ ] Widget de pantalla de inicio.

---

Desarrollado por **Adrián Alvarez** ([@Cordobot](https://github.com/Cordobot)) · Datos del clima por OpenWeather.

© 2026 Adrián Alvarez. Todos los derechos reservados.
