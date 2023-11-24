package com.running.runningmate2.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseActivity
import com.running.runningmate2.databinding.ActivityMainBinding
import com.running.runningmate2.ui.fragment.MapsFragment
import com.running.runningmate2.ui.fragment.RecordFragment
import com.running.runningmate2.ui.fragment.ResultFragment
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var bottomNav: BottomNavigationView
    private var presentLocation = R.id.home
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun initData() {

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true && it[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
            else {
                Toast.makeText(this, "위치 이용에 동의를 하셔야 이용 할 수 있습니다!", Toast.LENGTH_SHORT).show()
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

    override fun initUI() {
        loadFragment(MapsFragment())
        binding.customToolbar.bringToFront()
        bottomNav = findViewById(R.id.bottomNav)
    }

    override fun initListener() {
        binding.customToolbar.menu.getItem(0).setOnMenuItemClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            return@setOnMenuItemClickListener true
        }

        bottomNav.setOnItemSelectedListener {
            if(presentLocation == it.itemId)
                return@setOnItemSelectedListener true
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(MapsFragment())
                    presentLocation = it.itemId
                    true
                }
                R.id.recode -> {
                    loadFragment(RecordFragment())
                    presentLocation = it.itemId
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun initObserver() {}



    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.myFragMent, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun changeFragment(index: Int) {
        when (index) {
            // Map 로딩
            1 -> {
                binding.bottomNav.visibility = View.INVISIBLE
            }
            // 결과 페이지
            2 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.myFragMent, ResultFragment())
                    .commit()
            }
            // 메인 페이지
            3 -> {
                binding.bottomNav.visibility = View.VISIBLE
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.myFragMent, MapsFragment())
                    .commit()
            }
        }
    }
}