package com.samuel.worldcities.di

import com.samuel.worldcities.database.AppDatabase
import com.samuel.worldcities.repositories.CitiesRepository
import com.samuel.worldcities.services.CityService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    @ViewModelScoped
    fun getWeatherRepository(cityService: CityService, appDatabase: AppDatabase): CitiesRepository {
        return CitiesRepository(cityService, appDatabase)
    }
}