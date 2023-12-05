package com.running.runningmate2.ui.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.running.domain.model.RunningData
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.databinding.FragmentRecordBinding
import com.running.runningmate2.recyclerView.Adapter
import com.running.runningmate2.viewModel.fragmentViewModel.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : BaseFragment<FragmentRecordBinding>(R.layout.fragment_record) {

    private val viewModel: RecordViewModel by viewModels()
    private val adapter by lazy { Adapter() }

    override fun initData() {
        viewModel.readDB()
    }

    override fun initUI() {
        parentFragmentManager
            .beginTransaction()
            .replace(binding.recordGraphFrame.id, RecordGraphFragment(viewModel))
            .commit()
    }

    override fun initListener() {
        binding.recordChangeBtn.setOnClickListener {
            if (binding.recordChangeBtn.text == "기록보기") {
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.recordRecyclerFrame.id, RecordRecyclerFragment(viewModel))
                    .commit()
                binding.recordChangeBtn.text = "통계보기"
                binding.recordRecyclerFrame.visibility = View.VISIBLE
                binding.recordGraphFrame.visibility = View.INVISIBLE
            } else {
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.recordGraphFrame.id, RecordGraphFragment(viewModel))
                    .commit()
                binding.recordChangeBtn.text = "기록보기"
                binding.recordRecyclerFrame.visibility = View.INVISIBLE
                binding.recordGraphFrame.visibility = View.VISIBLE
            }
        }
    }

    override fun initObserver() {
        viewModel.runningData.observe(viewLifecycleOwner) { dataList ->
            Log.e("TAG", "record initObserver: $dataList", )
            adapter.datalist = dataList
            if (dataList.isNotEmpty()) {
                var seconde = 0
                var minute = 0
                var hour = 0
                var calorie = 0.0
                var step = 0
                var distance = 0.0
                var date = 0
                var nowDay = ""
                var _minute = ""
                var _seconde = ""
                var _hour = ""
                for ( data in dataList) {
                    Log.e("TAG", "record initObserver: $data", )
                    if (nowDay != data.now.split(" ")[0]) {
                        date++
                        nowDay = data.now.split(" ")[0]
                    }
                    seconde += data.time.split(":")[2].toInt()
                    minute += data.time.split(":")[1].toInt()
                    hour += data.time.split(":")[0].toInt()
                    calorie += data.calorie.split(" ")[0].toDouble()
                    step += data.step.split(" ")[0].toInt()
                    distance += data.distance.split(" ")[0].toDouble()
                }
                if (seconde >= 60) {
                    val gap = seconde/60
                    minute += seconde / 60
                    seconde -= 60 * gap
                }
                if (minute >= 60) {
                    val gap = minute/60
                    hour += minute / 60
                    minute -= 60 * gap
                }

                _seconde = if (seconde.toString().length == 1) "0$seconde"
                else seconde.toString()

                _minute = if (minute.toString().length == 1) "0$minute"
                else minute.toString()

                _hour = if(hour.toString().length == 1) "0$hour"
                else hour.toString()

                binding.recordRecodeBox.data = RunningData(
                    dayOfWeek = "${date}일",
                    time = "${_hour}시간 ${_minute}분 ${_seconde}초",
                    distance = "${String.format("%.2f", distance)} M",
                    calorie = "${String.format("%.2f", calorie)} Kcal",
                    step = "${step}걸음"
                )
            }else{
                binding.recordRecodeBox.data = RunningData(
                    dayOfWeek = "0일",
                    time = "00시간 00분 00초",
                    distance = "0.00 M",
                    calorie = "0.00 Kcal",
                    step = "0걸음"
                )
            }
        }
    }
}