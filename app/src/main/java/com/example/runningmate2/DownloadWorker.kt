//package com.example.runningmate2
//
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.work.CoroutineWorker
//import androidx.work.ForegroundInfo
//import androidx.work.WorkManager
//import androidx.work.WorkerParameters
//import com.example.runningmate2.repo.MyLocationRepo
//import com.google.android.gms.location.LocationAvailability
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationResult
//import kotlinx.coroutines.delay
//
//
//class DownloadWorker(context: Context, parameters: WorkerParameters) :
//    CoroutineWorker(context, parameters) {
//
//    private val notificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as
//                NotificationManager
//
//    override suspend fun doWork(): Result {
//        Log.e("TAG", "doWork: in!!", )
//        val progress = "Starting Download"
//        setForeground(createForegroundInfo())
//
//
////        for(i in 1..100){
////            Log.e("TAG", "createForegroundInfo : $i!!", )
////            delay(300L)
////        }
//
//        return Result.success()
//    }
//    private fun createForegroundInfo(): ForegroundInfo {
//        Log.e("TAG", "createForegroundInfo : in!!", )
//
////        val id = applicationContext.getString(0)
////        val title = applicationContext.getString(0)
////        val cancel = applicationContext.getString(0)
//        // This PendingIntent can be used to cancel the worker
////        val intent = WorkManager.getInstance(applicationContext)
////            .createCancelPendingIntent(getId())
//        val notification = NotificationCompat.Builder(applicationContext, "0")
//            .setContentTitle("title")
//            .setTicker("title")
//            .setContentText("progress")
//            .setSmallIcon(R.drawable.ic_nownow)
//            .setOngoing(true)
//            .build()
//
//        Log.e("TAG", "일단 노트피케이션은 넘어옴: ", )
//
//        object : LocationCallback() {
//            override fun onLocationAvailability(p0: LocationAvailability) {
//                super.onLocationAvailability(p0)
//            }
//
//            override fun onLocationResult(p0: LocationResult) {
//                Log.e("TAG", "onLocationResult 들어옴: ", )
//                super.onLocationResult(p0)
//                p0.lastLocation?.let { location ->
//                    Log.e(javaClass.simpleName, "download location : $location")
////                    _location.add(location)
////                    _setNowBtn.value = location
//                }
//            }
//        }.also {MyLocationRepo.nowLocation(MyApplication.getApplication(), it)}
//        return ForegroundInfo(0,notification)
//    }
//}