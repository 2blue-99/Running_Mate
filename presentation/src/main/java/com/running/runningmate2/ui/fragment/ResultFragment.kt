package com.running.runningmate2.ui.fragment

import androidx.fragment.app.activityViewModels
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.ui.activity.MainActivity
import com.running.runningmate2.databinding.FragmentResultBinding
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel

class ResultFragment : BaseFragment<FragmentResultBinding>(R.layout.fragment_result) {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun initData() {
        mainViewModel.readDB()
    }

    override fun initUI() {

    }

    override fun initListener() {
        binding.backBtn.setOnClickListener{
            (activity as MainActivity).changeFragment(3)
        }
    }

    override fun initObserver() {
        mainViewModel.runningData.observe(viewLifecycleOwner){datas ->
            if(datas.size>0){
                binding.todayData.text = datas.last().now
                binding.timeData.text = datas.last().time
                binding.distanceData.text = datas.last().distance
                binding.calorieData.text = datas.last().calorie
                binding.stepData.text = datas.last().step
            }

        }
    }
}