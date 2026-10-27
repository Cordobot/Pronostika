package cordobot.example.pronstika.data.repository

import cordobot.example.pronstika.data.remote.ForecastItem
import cordobot.example.pronstika.data.remote.Main
import cordobot.example.pronstika.data.remote.WeatherDescription
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherMappersTest {

    // 2026-01-01T00:00:00Z
    private val dayStart = 1_767_225_600L

    private fun item(hoursFromStart: Int, min: Double, max: Double, icon: String, pop: Double = 0.0) =
        ForecastItem(
            dt = dayStart + hoursFromStart * 3_600L,
            main = Main(temp = (min + max) / 2, feels_like = 0.0, humidity = 50, pressure = 1013, temp_min = min, temp_max = max),
            weather = listOf(WeatherDescription(description = icon, icon = icon)),
            pop = pop
        )

    @Test
    fun `daily forecast groups entries by local day with min, max and rain chance`() {
        val items = listOf(
            item(0, 10.0, 12.0, "night"),
            item(12, 15.0, 22.0, "midday", pop = 0.4),
            item(21, 11.0, 14.0, "evening", pop = 0.7),
            item(24 + 12, 8.0, 18.0, "tomorrow")
        )

        val daily = items.toDaily(timezoneOffset = 0)

        assertEquals(2, daily.size)
        assertEquals(10.0, daily[0].tempMin, 0.0)
        assertEquals(22.0, daily[0].tempMax, 0.0)
        assertEquals(0.7, daily[0].precipitationChance, 0.0)
        assertEquals("midday", daily[0].iconCode)
        assertEquals("tomorrow", daily[1].iconCode)
    }

    @Test
    fun `daily forecast uses the city offset to decide the day`() {
        // 03:00 UTC is still the previous day at UTC-5
        val items = listOf(item(-6, 5.0, 6.0, "a"), item(3, 7.0, 8.0, "b"))

        assertEquals(1, items.toDaily(timezoneOffset = -5 * 3_600).size)
        assertEquals(2, items.toDaily(timezoneOffset = 0).size)
    }

    @Test
    fun `daily forecast keeps at most five days and hourly the next eight entries`() {
        val items = (0 until 40).map { item(it * 3, 1.0, 2.0, "i") }

        assertEquals(5, items.toDaily(timezoneOffset = 0).size)
        assertEquals(8, items.toHourly().size)
        assertEquals(dayStart, items.toHourly().first().time)
    }
}
