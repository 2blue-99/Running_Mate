package com.running.runningmate2.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.running.runningmate2.databinding.FragmentRecordBinding
import com.running.runningmate2.recyclerView.Adapter
import com.running.runningmate2.model.Data
import com.running.runningmate2.model.toData
import com.running.runningmate2.viewModel.MainViewModel

class RecordFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding: FragmentRecordBinding by lazy {
        FragmentRecordBinding.inflate(layoutInflater)
    }
    private val adapter = Adapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.readDB()
        parentFragmentManager
            .beginTransaction()
            .replace(binding.graphFrameLayout.id, RecordGraphFragment())
            .commit()

        binding.changeBotton.setOnClickListener {
            if (binding.changeBotton.text == "기록보기") {
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.recyclerFrameLayout.id, RecordRecyclerFragment())
                    .commit()
                binding.changeBotton.text = "통계보기"
                binding.recyclerFrameLayout.visibility = View.VISIBLE
                binding.graphFrameLayout.visibility = View.INVISIBLE
            } else {
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.graphFrameLayout.id, RecordGraphFragment())
                    .commit()
                binding.changeBotton.text = "기록보기"
                binding.recyclerFrameLayout.visibility = View.INVISIBLE
                binding.graphFrameLayout.visibility = View.VISIBLE
            }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.runningData.observe(viewLifecycleOwner) { datas ->
            adapter.datalist = datas.map { it.toData() } as ArrayList<Data>
            if (datas.size > 0) {
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
                for (data in datas) {
                    if (nowDay != data.now.split(" ")[0]) {
                        date++
                        nowDay = data.now.split(" ")[0]
                    }
                    seconde += data.time.split(":")[2].toInt()
                    minute += data.time.split(":")[1].toInt()
                    hour += data.time.split(":")[0].toInt()
                    calorie += data.calorie.split(" ")[0].toDouble()
                    step += data.step.split(" ")[0].toInt()
                    distance += data.distance.split(" ")[0].toDouble() }
                if (seconde >= 60) {
                    val gap = seconde/60
                    minute += seconde / 60
                    seconde -= 60 * gap }
                if (minute >= 60) {
                    val gap = minute/60
                    hour += minute / 60
                    minute -= 60 * gap }

                _seconde = if (seconde.toString().length == 1) "0$seconde"
                else seconde.toString()

                _minute = if (minute.toString().length == 1) "0$minute"
                else minute.toString()

                _hour = if(hour.toString().length == 1) "0$hour"
                else hour.toString()

                binding.include.recodeDistance.text = "${String.format("%.2f", distance)} M"
                binding.include.recodeTime.text = "${_hour}시간 ${_minute}분 ${_seconde}초"
                binding.include.recodeCalorie.text = "${String.format("%.2f", calorie)} Kcal"
                binding.include.recodeDay.text = "${date}일"
                binding.include.recodeStep.text = "${step}걸음"
            }else{
                binding.include.recodeDistance.text = "0.00 M"
                binding.include.recodeTime.text = "00시간 00분 00초"
                binding.include.recodeCalorie.text = "0.00 Kcal"
                binding.include.recodeDay.text = "0일"
                binding.include.recodeStep.text = "0걸음"
            }
        }
    }
}