package com.running.runningmate2.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


abstract class BaseFragment<T: ViewDataBinding>(
    @LayoutRes val layoutRes: Int
): Fragment(){

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
        initUI()
        initListener()
        initObserver()
    }
    abstract fun initData()
    abstract fun initUI()
    abstract fun initListener()
    abstract fun initObserver()

    fun showShortToast(message: String?){
        context?.let {
            Toast.makeText(it, message ?: "", Toast.LENGTH_SHORT).show()
        }
    }

    fun showLongToast(message: String?){
        context?.let {
            Toast.makeText(it, message ?: "", Toast.LENGTH_LONG).show()
        }
    }
}