package com.running.runningmate2.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.running.runningmate2.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
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