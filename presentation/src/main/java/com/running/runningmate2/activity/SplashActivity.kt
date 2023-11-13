package com.running.runningmate2.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.running.runningmate2.R
import kotlinx.coroutines.delay

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        lifecycleScope.launchWhenCreated {
            delay(1000L)
            Intent(this@SplashActivity, MainActivity::class.java).also {
                startActivity(it)
                this@SplashActivity.finish()
            }
        }
    }
}