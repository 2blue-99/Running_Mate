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
                var day = 0
                var step = 0
                var distance = 0.0
                for(data in datas){
                    try {
                        seconde += data.time.split(":")[2].toInt()
                        minute += data.time.split(":")[1].toInt()
                        hour += data.time.split(":")[0].toInt()
                        calorie += data.calorie.split(" ")[0].toDouble()
                        day++
                        step += data.step.split(" ")[0].toInt()
                        distance += data.distance.split(" ")[0].toDouble()

                    }catch (e:Exception){
                        Log.e("TAG", "err : $e")
                    }
                }
                binding.include.recodeTime.text = "${hour}시간 ${minute}분 ${seconde}초"
                binding.include.recodeCalorie.text = "$calorie"
                binding.include.recodeDay.text = "${day}일"
                binding.include.recodeStep.text = "${step}걸음"
            }
        }

    }

    override fun onResume() {
        Log.e("TAG", "리줌리줌리줌리줌리줌리줌리줌 ")
        super.onResume()
    }
}