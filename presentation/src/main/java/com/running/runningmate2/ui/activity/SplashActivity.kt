package com.running.runningmate2.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseActivity
import com.running.runningmate2.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    override fun initData() {
        supportActionBar?.hide()
        lifecycleScope.launch {
            delay(1000L)
            Intent(this@SplashActivity, MainActivity::class.java).also {
                startActivity(it)
                this@SplashActivity.finish()
            }
        }
    }
    override fun initUI() {}

    override fun initListener() {}

    override fun initObserver() {}
}