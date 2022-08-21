package com.example.runningmate2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()


        lifecycleScope.launchWhenCreated {
            delay(2000L)
            Intent(this@SplashActivity, MainActivity::class.java).also {
                startActivity(it)
                this@SplashActivity.finish()
            }
        }

//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed(Runnable{
//            Intent(this, MainActivity::class.java).apply {
//                startActivity(this)
//                finish()
//            }
//        }, 2000)
    }
}