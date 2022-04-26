package com.samuel.worldcities.di


import com.samuel.worldcities.services.CityService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun getWeatherService(retrofit: Retrofit): CityService {
        return retrofit.create(CityService::class.java)
    }
}