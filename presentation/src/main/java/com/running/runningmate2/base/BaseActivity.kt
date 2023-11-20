package com.running.runningmate2.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


abstract class BaseActivity<T: ViewDataBinding>(
    @LayoutRes val layoutRes: Int
): AppCompatActivity(){

    private var backPressTime:Long = 0
    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        initData()
        initUI()
        initObserver()
        initListener()
    }

    abstract fun initData()
    abstract fun initUI()
    abstract fun initListener()
    abstract fun initObserver()

    override fun onBackPressed() {
        if(backPressTime + 2000 > System.currentTimeMillis())
            finish()
        else
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

        backPressTime = System.currentTimeMillis()
    }

    protected fun showShortToast(message: String?) {
        Toast.makeText(this, message ?: "", Toast.LENGTH_SHORT).show()
    }

    protected fun showLongToast(message: String?) {
        Toast.makeText(this, message ?: "", Toast.LENGTH_LONG).show()
    }
}