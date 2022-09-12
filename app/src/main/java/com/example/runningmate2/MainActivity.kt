package com.example.runningmate2

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.room.Room
import com.example.runningmate2.databinding.ActivityMainBinding
import com.example.runningmate2.fragment.MainMapsFragment
import com.example.runningmate2.fragment.RecordFragment
import com.example.runningmate2.fragment.ResultFragment
import com.example.runningmate2.fragment.RunningFragment
import com.example.runningmate2.room.AppDataBase
import com.example.runningmate2.viewModel.MainViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    // ActivityResultLauncher : 데이터를 받아옴, 이쪽ㅇ 페이지가 메모리 부족으로
    // 소멸될 때 콜백을 분리하여 이 페이지가 사라져도 다시 호출 가능.
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

//    private val db: AppDataBase = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "UserDB").build()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.e(javaClass.simpleName, "onCreate : start")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db: AppDataBase = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "UserDB").build()
        mainViewModel.getDao(db)

        loadFragment(MainMapsFragment())

        binding.customToolbar.bringToFront()
        binding.customToolbar.menu.getItem(0).setOnMenuItemClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            return@setOnMenuItemClickListener true
        }

        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
//                    Log.e(javaClass.simpleName, "onCreate: Main")
                    loadFragment(MainMapsFragment())
                    true
                }
                R.id.recode -> {
//                    Log.e(javaClass.simpleName, "onCreate: recode")
                    loadFragment(RecordFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

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
                    .replace(R.id.myFragMent, MainMapsFragment())
                    .commit()
            }
            4 -> {
//                supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.myFragMent, BottomSheet())
//                    .commit()
            }
        }
    }
}