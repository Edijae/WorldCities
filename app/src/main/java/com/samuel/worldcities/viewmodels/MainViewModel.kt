package com.samuel.worldcities.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.samuel.worldcities.models.City
import com.samuel.worldcities.repositories.CitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject() constructor(val citiesRepository: CitiesRepository) : ViewModel() {

    private var filter: String? = null


    fun searchCities(filter: String?): Flow<PagingData<City>> {
        val newData = (this.filter == null || !this.filter.contentEquals(filter))
        this.filter = filter
        return citiesRepository.getCities(filter, newData).flow
            .cachedIn(viewModelScope)
    }
}