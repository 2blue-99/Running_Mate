package com.example.runningmate2

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object MyLocationRepo {

    suspend fun nowLocation(application : Application): Location? {

        // FusedLocationProvider : 개발자가 위치를 획득할 수 있음. Fused(결합된)
        // 안드로이드에서 위치정보를 얻기 위해  LocationManager와 FusedLocationProvider가 있음
        // 후자가 배터리 효율도 좋고 공식권장하기 때문에 사용.
        // 위치 정보를 제공하는 클라이언트 객체 생성.
        val locationClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

        // ContextCompat : Resource에서 값을 가져오거나 퍼미션을 확인할 때 사용할 때
        // SDK버전을 고려하지 않아도 되도록 (내부적으로SDK버전을 처리해둔) 클래스입니다
        // ContextCompat.checkSelfPermission으로 권한을 확인함.
        // 인자로 application과 원하는거(permission) 를 인자로 받음?
        // Context의 App이 인자로 전달된 퍼미션을 갖고 있다면 PackageManager.PERMISSION_GRANTED를 리턴합니다.
        // 그렇지 않다면 PackageManager.PERMISSION_DENIED을 리턴합니다.
        // 즉, 이 API는 자신의 앱의 퍼미션 상태만 확인할 수 있습니다.
        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // application은 어느 컴포넌트에서나 공유할 수 있는 전역 클래스를 의미
        // application을 상속받은 클레스는 공동으로 관리해야 하는 데이터를 관리해야 하는 데이터를 작성하기에 적합
        // 클래스를 만들고 Application 클래스를 상속한 뒤, 매니페스트의 android:name 속성에 등록해서 사용한다
        // 어떤 값을 액티비티, 서비스 등 안드로이드 컴포넌트들 사이에서 공유해 사용할 수 있게 해준다
        // Application을 상속받은 클래스는 1번째 액티비티보다 먼저 인스턴스화된다
        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            return null
        }

        return suspendCancellableCoroutine { cont ->
            locationClient.lastLocation.apply {
                if(isComplete) {
                    if (isSuccessful) {
                        cont.resume(result)
                    }
                    else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }

                addOnSuccessListener {
                    cont.resume(it)
                }

                addOnFailureListener {
                    cont.resume(null)
                }

                addOnCanceledListener {
                    cont.cancel()
                }
            }
        }
    }
}