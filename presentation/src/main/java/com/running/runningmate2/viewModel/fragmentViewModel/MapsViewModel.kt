package com.running.runningmate2.viewModel.fragmentViewModel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.running.data.local.sharedPreference.SharedPreferenceHelperImpl
import com.running.domain.SavedData.DomainWeather
import com.running.domain.state.ResourceState
import com.running.domain.usecase.GetWeatherUseCase
import com.running.domain.usecase.LocationUseCase
import com.running.runningmate2.base.BaseViewModel
import com.running.runningmate2.utils.ListLiveData
import com.running.runningmate2.utils.MapState
import com.running.runningmate2.utils.WeatherHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val locationUseCase: LocationUseCase,
    private val sharedPreferenceHelperImpl: SharedPreferenceHelperImpl
) : BaseViewModel(){

    private val _weatherData = MutableLiveData<ResourceState<DomainWeather>>()
    val weatherData: LiveData<ResourceState<DomainWeather>> get() = _weatherData
    fun isWeatherLoading() = _weatherData.value is ResourceState.Loading

    private val _mapState = MutableLiveData<MapState>()
    val mapState: LiveData<MapState> get() = _mapState
    fun changeState(state: MapState){ _mapState.value = state }

    private val _location = ListLiveData<Location>()
    val location: LiveData<ArrayList<Location>> get() = _location
    fun getNowLocation(): Location? = _location.value?.last()
    fun getNowLatLng(): LatLng? = _location.value?.last()?.let { LatLng(it.latitude, it.longitude) }

    val loading: LiveData<Boolean> get() = isLoading

    private var locationStream: Job? = null
    fun startLocationStream(){
        locationStream = locationUseCase.getLocationDataStream().onEach {
            when (it) {
                is ResourceState.Success -> {
                    isLoading.value = false
                    _location.add(it.data)
                }
                else -> {
                    isLoading.value = true
                    _location.add(Location(null))
                }
            }
        }.launchIn(modelScope)
    }
    fun stopLocationStream() { locationStream?.cancel() }
    fun getWeatherData() {
        modelScope.launch {
            WeatherHelper.makeRequest(_location.value?.last()).let { request ->
                getWeatherUseCase(request).onEach {
                    _weatherData.postValue(it)
                }.catch {
                    _weatherData.postValue(ResourceState.Error(message = "UnHandle Err"))
                }.launchIn(modelScope)
            }
        }
    }
    fun saveWeight(weight: String) = sharedPreferenceHelperImpl.saveWeight(weight.toInt())
    fun getWeight(): Int = sharedPreferenceHelperImpl.getWeight()
}
