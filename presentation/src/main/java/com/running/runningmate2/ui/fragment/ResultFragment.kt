package com.running.runningmate2.ui.fragment

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.ui.activity.MainActivity
import com.running.runningmate2.databinding.FragmentResultBinding
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel
import com.running.runningmate2.viewModel.fragmentViewModel.ResultViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultFragment : BaseFragment<FragmentResultBinding>(R.layout.fragment_result) {

    private val activityViewModel: MainViewModel by activityViewModels()
    override fun initData() {}
    override fun initUI() {
        if(activityViewModel.savedData != null)
            binding.dataList = activityViewModel.savedData
    }

    override fun initListener() {
        binding.backBtn.setOnClickListener{
            (activity as MainActivity).changeFragment(3)
        }
    }

    override fun initObserver() {

    }

//    binding.todayData.text = datas.last().now
//    binding.timeData.text = datas.last().time
//    binding.distanceData.text = datas.last().distance
//    binding.calorieData.text = datas.last().calorie
//    binding.stepData.text = datas.last().step
}