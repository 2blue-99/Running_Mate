package com.running.runningmate2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.running.runningmate2.activity.MainActivity
import com.running.runningmate2.databinding.FragmentResultBinding
import com.running.runningmate2.viewModel.MainViewModel

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater,container, false)
        val view = binding.root
        mainViewModel.readDB()
        binding.backBtn.setOnClickListener{
            (activity as MainActivity).changeFragment(3)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //데이터 불러오기
        super.onViewCreated(view, savedInstanceState)
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