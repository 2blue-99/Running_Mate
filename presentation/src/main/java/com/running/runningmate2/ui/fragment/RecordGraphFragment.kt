package com.running.runningmate2.ui.fragment

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.running.runningmate2.utils.MyApplication
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.databinding.FragmentRecordGraphBinding
import com.running.runningmate2.utils.GraphHelper
import com.running.runningmate2.viewModel.fragmentViewModel.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecordGraphFragment(
    viewModel: RecordViewModel
) : BaseFragment<FragmentRecordGraphBinding>(R.layout.fragment_record_graph) {
    private val mainViewModel = viewModel
    override fun initData() {}

    override fun initUI() {}

    override fun initListener() {}

    override fun initObserver() {
        mainViewModel.runningData.observe(viewLifecycleOwner){ datas->
            if(datas.isNotEmpty()){
                binding.recordGraphFrame.setBackgroundColor(Color.WHITE)
                binding.recordGraphEmptyTxt.visibility = View.INVISIBLE
                binding.recordGraphChart.visibility = View.VISIBLE
                GraphHelper(datas, binding.recordGraphChart)
            }else{
                binding.recordGraphFrame.setBackgroundColor(Color.parseColor("#4DFFFFFF"))
                binding.recordGraphEmptyTxt.visibility = View.VISIBLE
                binding.recordGraphEmptyTxt.text = "통계 데이터가 없어요.."
                binding.recordGraphChart.visibility = View.INVISIBLE
            }
        }
    }
}