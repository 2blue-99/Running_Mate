package com.example.runningmate2.viewModel

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import androidx.room.Room
import com.example.data.RepoImpl
import com.example.domain.model.DomainWeather
import com.example.runningmate2.*
import com.example.runningmate2.room.AppDataBase
import com.example.runningmate2.room.Dao
import com.example.runningmate2.room.Entity
import com.example.runningmate2.utils.ListLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDateTime

class MainViewModel : ViewModel(){
    private val _myValue = MutableLiveData<DomainWeather?>()
    val myValue: LiveData<DomainWeather?> get() = _myValue

    lateinit var myDataList : DomainWeather

    private var dao: Dao? = null

    private val _runningData = ListLiveData<Entity>()
    val runningData: LiveData<ArrayList<Entity>> get() = _runningData

    @RequiresApi(Build.VERSION_CODES.O)
    fun createRequestParams(myLocation:Location?): HashMap<String, String> {
        val now = LocalDateTime.now()
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
            when {
                now.monthValue > 10 && now.dayOfMonth > 10 -> "${now.year}${now.monthValue}${now.dayOfMonth}"
                now.monthValue > 10 && now.dayOfMonth < 10 -> "${now.year}${now.monthValue}0${now.dayOfMonth}"
                now.monthValue < 10 && now.dayOfMonth > 10 -> "${now.year}0${now.monthValue}${now.dayOfMonth}"
                now.monthValue < 10 && now.dayOfMonth < 10 -> "${now.year}0${now.monthValue}0${now.dayOfMonth}"
                else -> "20220801"
            }
        } else {
            val date =
                if (baseTime != "0000") now.minusDays(1)
                else now

            when {
                date.monthValue > 10 && date.dayOfMonth > 10 -> "${date.year}${date.monthValue}${date.dayOfMonth}"
                date.monthValue > 10 && date.dayOfMonth < 10 -> "${date.year}${date.monthValue}0${date.dayOfMonth}"
                date.monthValue < 10 && date.dayOfMonth > 10 -> "${date.year}0${date.monthValue}${date.dayOfMonth}"
                date.monthValue < 10 && date.dayOfMonth < 10 -> "${date.year}0${date.monthValue}0${date.dayOfMonth}"
                else -> "20220801"
            }
        }

        val locate = myLocation?.let { TransLocationUtil.convertLocation(it) }
        Log.e(javaClass.simpleName, "locate?.nx: ${locate?.nx}, locate?.ny: ${locate?.ny}")

        return HashMap<String, String>().apply {
            put("serviceKey", BuildConfig.SERVICE_KEY)
            put("pageNo", "1")
            put("numOfRows", "10")
            put("dataType", "JSON")
            put("base_date", baseDate)
            put("base_time", baseTime)
            put("nx", locate?.nx?.toInt().toString() ?: "55" )
            put("ny", locate?.ny?.toInt().toString() ?: "127")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeatherData(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = createRequestParams(location)
                myDataList = RepoImpl().RepoGetWeatherData(data)
                Log.e(javaClass.simpleName, "get_Data: $myDataList", )
                _myValue.postValue(myDataList)

            }catch (e:Exception){
                Log.e(javaClass.simpleName, "@@@@@@ My Err: ${e.localizedMessage}", )
            }
        }
    }

    init {
        getData()
    }



    ///데이터저장
    suspend fun putData(runningData: RunningData){
        if(dao == null){
            return
        }
        dao?.putData(Entity(runningData.time,runningData.distance,runningData.calorie,runningData.step))
    }

    fun getData(){
        if(dao == null){
            return
        }
        _runningData.clear()
        dao?.getData()?.onEach {
            _runningData.clear()
            _runningData.addAll(it)
        }?.launchIn(viewModelScope)
    }

    fun getDao(db : AppDataBase){
        dao = db.getDao()
    }

    fun clearWeatherData() {
        _myValue.value = null
    }


}