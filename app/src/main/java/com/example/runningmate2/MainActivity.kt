package com.example.runningmate2

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.example.runningmate2.databinding.ActivityMainBinding
import com.example.runningmate2.fragment.MainMapsFragment
import com.example.runningmate2.fragment.RecordFragment
import com.example.runningmate2.fragment.ResultFragment
import com.example.runningmate2.fragment.RunningFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(javaClass.simpleName, "onCreate : start")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            Log.e(javaClass.simpleName, "model.getLocation() :", )
        }
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        loadFragment(MainMapsFragment())

        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    Log.e(javaClass.simpleName, "onCreate: Main")
                    loadFragment(MainMapsFragment())
                    true
                }
                R.id.recode -> {
                    Log.e(javaClass.simpleName, "onCreate: recode")
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
        Log.e(javaClass.simpleName, "loadFragment : ")
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
        }
    }
}