package com.running.runningmate2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
//            window.setDecorFitsSystemWindows(false)
//            val controller = window.insetsController
//            if(controller != null){
//                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
//                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            }
//        }else{
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }


        lifecycleScope.launchWhenCreated {
            delay(1000L)
            Intent(this@SplashActivity, MainActivity::class.java).also {
                startActivity(it)
                this@SplashActivity.finish()
            }
        }
    }
}