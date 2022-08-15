package com.example.runningmate2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.runningmate2.databinding.ActivityMainBinding
import com.example.runningmate2.fragment.MainStartPage

class MainActivity : AppCompatActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var binding : ActivityMainBinding
    var _nowLocation: Location? = null
    var myLatitude = 0.0
    var myLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(javaClass.simpleName, "onCreate: start", )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            // 값이 null값인 경우와 제대로 된 경우
            _nowLocation = NowLocation()
            if(_nowLocation?.latitude.toString() == "null"){
                myLatitude = 36.79523646
                myLongitude = 127.10808029
                Log.e(javaClass.simpleName, "myFakeLocation: $myLatitude, $myLongitude", )
                Toast.makeText(this,"위치를 불러올 수 없습니다.",Toast.LENGTH_LONG)
            }
            else{
                myLatitude = _nowLocation?.latitude!!
                myLongitude = _nowLocation?.longitude!!
                Log.e(javaClass.simpleName, "myRealLocation: $myLatitude, $myLongitude", )
            }
            // 값 가져와 넘기기
            var mainStartPage = MainStartPage()
            var bundle = Bundle()
            bundle.putDouble("myLatitude",myLatitude)
            bundle.putDouble("myLongitude",myLongitude)

            mainStartPage.arguments = bundle

            Log.e(javaClass.simpleName, "send Bundle : $myLatitude, $myLongitude", )

            supportFragmentManager.beginTransaction()
                .replace(R.id.myFragMent, mainStartPage)
                .addToBackStack(null)
                .commit()
        }
        // 여기가 실질적 권한 받는곳, 이쪽으로 인해 폰 켰을 때 권한 유무 뜸
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))




////        액션바 사라지기
//        val actionbar = this.supportActionBar
//        actionbar?.hide()



    }

    fun NowLocation(): Location? {
        Log.e(javaClass.simpleName, "NowLocation: hello", )

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return if(!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
//            Log.e(javaClass.simpleName, "return Null")
            null
        }else{
//            Log.e(javaClass.simpleName, "return gap")
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
    }
}