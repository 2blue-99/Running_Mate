package com.running.runningmate2.viewModel

import android.annotation.SuppressLint
import android.icu.util.LocaleData
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.running.data.RepoImpl
import com.running.domain.model.DomainWeather
import com.running.runningmate2.MyApplication
import com.running.runningmate2.RunningData
import com.running.runningmate2.TransLocationUtil
import com.running.runningmate2.recyclerView.Data
import com.running.runningmate2.repo.SharedPreferenceHelper
import com.running.runningmate2.repo.SharedPreferenceHelperImpl
import com.running.runningmate2.room.AppDataBase
import com.running.runningmate2.room.Dao
import com.running.runningmate2.room.Entity
import com.running.runningmate2.utils.Event
import com.running.runningmate2.utils.ListLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainViewModel : ViewModel(){
    private val _getWeatherData = MutableLiveData<DomainWeather?>()
    val getWeatherData: LiveData<DomainWeather?> get() = _getWeatherData

    lateinit var myDataList : DomainWeather

    private var dao: Dao? = null

    private val _runningData = ListLiveData<Entity>()
    val runningData: LiveData<ArrayList<Entity>> get() = _runningData

    private val _error = MutableLiveData<Event<String>>()
    val error: LiveData<Event<String>> get() = _error

    private val _success = MutableLiveData<Event<Unit>>()
    val success: LiveData<Event<Unit>> get() = _success

    private val helper: SharedPreferenceHelper = SharedPreferenceHelperImpl()


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun createRequestParams(myLocation:Location?): HashMap<String, String> {
        val now = LocalDateTime.now()
        Log.e("TAG", "now : $now", )
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
            LocalDate.now().toString().replace("-","")
        } else {
            val myDate = SimpleDateFormat("yyyy/MM/dd")
            val calendar = Calendar.getInstance()
            val today = Date()
            calendar.time = today
            calendar.add(Calendar.DATE, -1)
            myDate.format(calendar.time).replace("/","")
        }
        Log.e("TAG", "baseDate: $baseDate", )
        val locate = myLocation?.let { TransLocationUtil.convertLocation(it) }
        Log.e(javaClass.simpleName, "locate?.nx: ${locate?.nx}, locate?.ny: ${locate?.ny}")

        return HashMap<String, String>().apply {
            put("serviceKey", com.running.runningmate2.BuildConfig.SERVICE_KEY)
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
//                throw Exception("강제 에러")
                val data = createRequestParams(location)
                myDataList = RepoImpl().RepoGetWeatherData(data)
                _getWeatherData.postValue(myDataList)

            }catch (e:Exception){
                Log.e(javaClass.simpleName, "My Err: ${e.localizedMessage}", )
                val fakeData = DomainWeather(temperatures=0.0.toString(), rn1=0.0.toString(), eastWestWind=0.0.toString(), southNorthWind=0.0.toString(), humidity=0.0.toString(), rainType=0.0.toString(), windDirection=0.0.toString(), windSpeed=0.0.toString())
                _getWeatherData.postValue(fakeData)
//                _error.value = Event("현재 날씨 데이터를 이용하실 수 없습니다.")
            }
        }
    }

    init {
        readDB()
    }

    ///데이터저장
    suspend fun insertDB(runningData: RunningData){
        if(dao == null){
            return
        }
//        Log.e(
//            javaClass.simpleName,
//            "insertDB: ${Entity(LocalDateTime.now(),runningData.time,runningData.distance,runningData.calorie,runningData.step)}"
//        )
        dao?.insertData(Entity(0,runningData.dayOfWeek,runningData.now,runningData.time,runningData.distance,runningData.calorie,runningData.step))
//        myCount++
    }

    fun readDB(){
        if(dao == null){
            return
        }
        _runningData.clear()
        dao?.readAllData()?.onEach {
            Log.e(javaClass.simpleName, "여기는. 룸 읽기 : $it", )
            _runningData.clear()
            _runningData.addAll(it)
        }?.launchIn(viewModelScope)
    }

    suspend fun deleteDB(data: Data){
//        dao?.deleteData(Entity(runningData.time,runningData.distance,runningData.calorie,runningData.step))
        dao?.deleteData(data.id)
    }

    fun getDao(db : AppDataBase){
        dao = db.getDao()
    }

    // stop버튼을 눌렀을때 날씨정보를 새로 가져오기 위해 null로 지정
    fun clearWeatherData() {
        _getWeatherData.value = null
    }

    /**
     * SharedPreference 에 저장할 함수.
     * Float 형태가 아니면 터지기 때문에 try-catch 로 안정성 확보.
     */
    fun setData(weight: String) {
        try {
            helper.weight = weight.toInt()
            _success.value = Event(Unit)
        } catch (t: Throwable) {
            Log.e(javaClass.simpleName, "setWeightLocalStorage: ${t.localizedMessage}", )
            _error.value = Event("실수 형태로 입력 해라...")
        }
    }
    /**
     * SharedPreference 에서 몸무게를 가져 오는 함수.
     */
    fun getWeight(): Int {
        return helper.weight
    }

    /**
     * Test 용도.
     * SharedPreference Clear
     */
    fun clearLocalStorage() {
        helper.clear()
    }

}