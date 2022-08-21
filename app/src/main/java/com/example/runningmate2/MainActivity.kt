package com.example.runningmate2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.runningmate2.databinding.ActivityMainBinding
import com.example.runningmate2.fragment.MainStartFragment
import com.example.runningmate2.fragment.RecordFragment
import com.example.runningmate2.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var binding: ActivityMainBinding
    var _nowLocation: Location? = null
    var myLatitude = 0.0
    var myLongitude = 0.0
    private val model: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.e(javaClass.simpleName, "onCreate : start")

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //위치 권한 관련 함수, 위도 경도 가져오고, API까지 호출, 화면 전환까지.
        LocationFun()

        // 여기가 실질적 권한 받는곳, 이쪽으로 인해 폰 켰을 때 권한 유무 뜸
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun LocationFun(){
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            // 값이 null값인 경우와 제대로 된 경우
            // 해당 함수를 통해 위도 경도를 가져옴
            _nowLocation = NowLocation()
            if (_nowLocation?.latitude.toString() == "null") {
                myLatitude = 36.79523646
                myLongitude = 127.10808029
                Log.e(javaClass.simpleName, "myFakeLocation: $myLatitude, $myLongitude")
                Toast.makeText(this, "위치를 불러올 수 없습니다.", Toast.LENGTH_LONG)
            } else {
                myLatitude = _nowLocation?.latitude!!
                myLongitude = _nowLocation?.longitude!!
                Log.e(javaClass.simpleName, "myRealLocation: $myLatitude, $myLongitude")
            }
            //가져온 위도경도를 해당 함수에 넣어 API를 통해 데이터 가져옴
            model.getWeatherData(model.createRequestParams(_nowLocation))

            // 값 가져와 Main Fragment에 넘기기 여기서 만든 객체를 replace쪽에도 사용해야함
            var mainStartPage = MainStartFragment()
            var bundle = Bundle()
            bundle.putDouble("myLatitude", myLatitude)
            bundle.putDouble("myLongitude", myLongitude)
            mainStartPage.arguments = bundle
            Log.e(javaClass.simpleName, "send Bundle : $myLatitude, $myLongitude")

            loadFragment(mainStartPage)

            bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        Log.e(javaClass.simpleName, "onCreate: Main")
                        loadFragment(mainStartPage)
                        true
                    }
                    R.id.recode -> {
                        Log.e(javaClass.simpleName, "onCreate: recode")
                        loadFragment(RecordFragment())
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        Log.e(javaClass.simpleName, "loadFragment : ")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.myFragMent, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    fun NowLocation(): Location? {
        Log.e(javaClass.simpleName, "NowLocation: hello")

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
//            Log.e(javaClass.simpleName, "return Null")
            null
        } else {
//            Log.e(javaClass.simpleName, "return gap")
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
    }
}