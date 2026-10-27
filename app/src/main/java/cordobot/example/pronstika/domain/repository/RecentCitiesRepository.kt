package cordobot.example.pronstika.domain.repository

interface RecentCitiesRepository {
    fun getRecentCities(): List<String>

    /** Saves [city] as the most recent search and returns the updated list. */
    fun addCity(city: String): List<String>

    fun removeCity(city: String): List<String>
}
