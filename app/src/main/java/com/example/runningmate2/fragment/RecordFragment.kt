package com.example.runningmate2.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.runningmate2.databinding.FragmentRecordBinding
import com.example.runningmate2.recyclerView.Adapter
import com.example.runningmate2.recyclerView.Data
import com.example.runningmate2.recyclerView.toData
import com.example.runningmate2.viewModel.MainViewModel
import java.lang.Exception
import kotlin.math.min

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
            .replace(binding.recordeFrameLayout.id, RecordGraphFragment())
            .commit()

        binding.changeBotton.setOnClickListener{
            if(binding.changeBotton.text == "기록보기"){
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.recordeFrameLayout.id, RecordRecyclerFragment())
                    .commit()
                binding.changeBotton.text = "통계보기"
            }else{
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.recordeFrameLayout.id, RecordGraphFragment())
                    .commit()
                binding.changeBotton.text = "기록보기"
            }
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.runningData.observe(viewLifecycleOwner) { data ->
            adapter.datalist = data.map { it.toData() } as ArrayList<Data>
        }

        mainViewModel.runningData.observe(viewLifecycleOwner){datas->
            Log.e("TAG", "@@@@@@@@@@@@@@@@@@@@ : $datas", )
            if(datas.size>0){
                var seconde = 0
                var minute = 0
                var hour = 0
                var calorie = 0.0
                var step = 0
                var distance = 0.0
                var firstDate = datas.first().now.split(" ")[0]
                var date = 1
                var nowDate = ""

                for(data in datas){
                    try {
                        nowDate = data.now.split(" ")[0]
                        seconde += data.time.split(":")[2].toInt()
                        if(seconde>=60){
                            minute += seconde/60
                            seconde = seconde - (60 * minute)
                        }
                        minute += data.time.split(":")[1].toInt()
                        if(minute>=60){
                            hour += minute/60
                            minute -= 60 * hour
                        }
                        hour += data.time.split(":")[0].toInt()
                        calorie += data.calorie.split(" ")[0].toDouble()
                        step += data.step.split(" ")[0].toInt()
                        distance += data.distance.split(" ")[0].toDouble()

                        if(firstDate != nowDate){
                            date++
                            firstDate = nowDate
                        }

                    }catch (e:Exception){
                        Log.e("TAG", "err : $e")
                    }
                }
//                {String.format("%.2f",distanceHap + result)}
                binding.include.recodeDistance.text = "${String.format("%.2f",distance)} M"
                binding.include.recodeTime.text = "${hour}시간 ${minute}분 ${seconde}초"
                binding.include.recodeCalorie.text = "${String.format("%.2f",calorie)} Kcal"
                binding.include.recodeDay.text = "${date}일"
                binding.include.recodeStep.text = "${step}걸음"
            }
        }

    }
}