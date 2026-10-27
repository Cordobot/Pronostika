package cordobot.example.pronstika.data.local

import android.content.Context
import cordobot.example.pronstika.domain.repository.RecentCitiesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PREFS_NAME = "pronostika_prefs"
private const val KEY_RECENT = "recent_cities"
private const val SEPARATOR = "|"
private const val MAX_RECENT = 5

/** Keeps the last searched cities (most recent first) in SharedPreferences. */
class RecentCitiesStorage @Inject constructor(
    @ApplicationContext context: Context
) : RecentCitiesRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getRecentCities(): List<String> =
        prefs.getString(KEY_RECENT, null)
            ?.split(SEPARATOR)
            ?.filter { it.isNotBlank() }
            .orEmpty()

    override fun addCity(city: String): List<String> {
        val updated = (listOf(city) + getRecentCities().filterNot { it.equals(city, ignoreCase = true) })
            .take(MAX_RECENT)
        save(updated)
        return updated
    }

    override fun removeCity(city: String): List<String> {
        val updated = getRecentCities().filterNot { it.equals(city, ignoreCase = true) }
        save(updated)
        return updated
    }

    private fun save(cities: List<String>) {
        prefs.edit().putString(KEY_RECENT, cities.joinToString(SEPARATOR)).apply()
    }
}
