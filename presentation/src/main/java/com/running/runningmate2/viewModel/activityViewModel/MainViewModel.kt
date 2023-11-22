package com.running.runningmate2.viewModel.activityViewModel

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.running.data.local.sharedPreference.SharedPreferenceHelperImpl
import com.running.domain.SavedData.DomainWeather
import com.running.domain.usecase.GetWeatherUseCase
import com.running.runningmate2.base.BaseViewModel
import com.running.runningmate2.utils.TransLocationUtil
import com.running.domain.model.RunningData
import com.running.domain.usecase.LocalDataUseCase
import com.running.runningmate2.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val localDataUseCase: LocalDataUseCase,
    private val sharedPreferenceHelperImpl: SharedPreferenceHelperImpl
) : BaseViewModel() {
    private val _getWeatherData = MutableLiveData<DomainWeather?>()
    val getWeatherData: LiveData<DomainWeather?> get() = _getWeatherData


    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> get() = _error

    private val _success = MutableLiveData<Event<Unit>>()
    val success: LiveData<Event<Unit>> get() = _success

    var savedData: RunningData? = null
    private lateinit var myDataList: DomainWeather


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun createRequestParams(myLocation: Location?): HashMap<String, String> {
        val now = LocalDateTime.now()
        Log.e("TAG", "now : $now")
        val baseTime = when {
            now.hour > 11 -> {
                if (now.minute < 40) "${now.hour - 1}00"
                else "${now.hour}00"
            }

            now.hour == 10 -> {
                if (now.minute < 40) "0900"
                else "1000"
            }

            now.hour in 1..9 -> {
                if (now.minute < 40) "0${now.hour - 1}00"
                else "0${now.hour}00"
            }

            now.hour == 0 -> {
                if (now.minute < 40) "2300"
                else "0000"
            }

            else -> "0000"
        }

        val baseDate = if (now.hour != 0) {
            LocalDate.now().toString().replace("-", "")
        } else {
            val myDate = SimpleDateFormat("yyyy/MM/dd")
            val calendar = Calendar.getInstance()
            val today = Date()
            calendar.time = today
            calendar.add(Calendar.DATE, -1)
            myDate.format(calendar.time).replace("/", "")
        }
        val locate = myLocation?.let { TransLocationUtil.convertLocation(it) }

        return HashMap<String, String>().apply {
            put("serviceKey", com.running.runningmate2.BuildConfig.SERVICE_KEY)
            put("pageNo", "1")
            put("numOfRows", "10")
            put("dataType", "JSON")
            put("base_date", baseDate)
            put("base_time", baseTime)
            put("nx", locate?.nx?.toInt().toString() ?: "55")
            put("ny", locate?.ny?.toInt().toString() ?: "127")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherData(location: Location) {
        modelScope.launch {
            try {
                val data = createRequestParams(location)
                myDataList = getWeatherUseCase(data)
                _getWeatherData.postValue(myDataList)
            } catch (e: Exception) {
                val fakeData = DomainWeather(
                    temperatures = 0.0.toString(),
                    rn1 = 0.0.toString(),
                    eastWestWind = 0.0.toString(),
                    southNorthWind = 0.0.toString(),
                    humidity = 0.0.toString(),
                    rainType = 0.0.toString(),
                    windDirection = 0.0.toString(),
                    windSpeed = 0.0.toString()
                )
                _getWeatherData.postValue(fakeData)
            }
        }
    }
    suspend fun insertDB(runningData: RunningData) {
        savedData = runningData
        ioScope.launch {
            localDataUseCase.insertData(runningData)
        }
    }
    fun setData(weight: String) {
        try {
            sharedPreferenceHelperImpl.saveWeight(weight.toInt())
            _success.value = Event(Unit)
        } catch (t: Throwable) {
            _error.value = Event("실수 형태로 입력하세요.")
        }
    }

    fun getWeight(): Int = sharedPreferenceHelperImpl.getWeight()
}