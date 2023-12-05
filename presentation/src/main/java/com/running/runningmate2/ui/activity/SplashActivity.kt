package com.running.runningmate2.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseActivity
import com.running.runningmate2.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    override fun initData() {

        supportActionBar?.hide()

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true && it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                lifecycleScope.launch {
                    delay(1000L)
                    Intent(this@SplashActivity, MainActivity::class.java).also {
                        startActivity(it)
                        this@SplashActivity.finish()
                    }
                }
            }
            else {
                showShortToast("위치 이용에 동의를 하셔야 이용 할 수 있습니다!")
                this.finish()
            }
        }
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    override fun initUI() {}

    override fun initListener() {}

    override fun initObserver() {}
}