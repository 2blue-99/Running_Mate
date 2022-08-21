package com.example.runningmate2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.runningmate2.databinding.ActivityUserDataBinding

private lateinit var binding : ActivityUserDataBinding

class UserDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextBtn.setOnClickListener {
            if(binding.Height.text)

            Intent(this@UserDataActivity, MainActivity::class.java).also {
                startActivity(it)
                this@UserDataActivity.finish()
            }
        }



        //데이터 바인딩해서 넘기기
    }
}