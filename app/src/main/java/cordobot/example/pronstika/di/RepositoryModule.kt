package cordobot.example.pronstika.di

import cordobot.example.pronstika.data.local.RecentCitiesStorage
import cordobot.example.pronstika.data.repository.WeatherRepositoryImpl
import cordobot.example.pronstika.domain.repository.RecentCitiesRepository
import cordobot.example.pronstika.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindRecentCitiesRepository(
        recentCitiesStorage: RecentCitiesStorage
    ): RecentCitiesRepository
}
