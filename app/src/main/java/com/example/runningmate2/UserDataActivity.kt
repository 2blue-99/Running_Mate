package com.example.runningmate2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.runningmate2.databinding.ActivityUserDataBinding

private lateinit var binding : ActivityUserDataBinding

class UserDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextBtn.setOnClickListener {
            Log.e(javaClass.simpleName, "user data class : ${binding.Height.text}", )

            if(binding.Height.text.length == 3 && binding.weight.text.length >= 2 ){
                Intent(this@UserDataActivity, MainActivity::class.java).also {
                    startActivity(it)
                    this@UserDataActivity.finish() }
            }else{
                Toast.makeText(this, "정확한 값을 입력해 주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }
}