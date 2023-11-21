package com.running.runningmate2.ui.activity

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData() {

        loadFragment(MapsFragment())

        binding.customToolbar.bringToFront()
        binding.customToolbar.menu.getItem(0).setOnMenuItemClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            return@setOnMenuItemClickListener true
        }

        bottomNav = findViewById(R.id.bottomNav)
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

    override fun initUI() {}

    override fun initListener() {}

    override fun initObserver() {}



    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.myFragMent, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun changeFragment(index: Int) {
        when (index) {
            1 -> {
                binding.bottomNav.visibility = View.INVISIBLE
            }
            //결과페이지로 이동
            2 -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.myFragMent, ResultFragment())
                    .commit()
            }
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