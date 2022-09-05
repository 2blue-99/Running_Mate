package com.example.runningmate2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.runningmate2.MainActivity
import com.example.runningmate2.databinding.FragmentResultBinding
import com.example.runningmate2.viewModel.MainViewModel
import java.time.LocalDateTime
import java.util.*

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    val currentTime = LocalDateTime.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        //뷰 만들기/
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater,container, false)
        val view = binding.root
        mainViewModel.getData()
//        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
//
//        }

        binding.backBtn.setOnClickListener{
            (activity as MainActivity).changeFragment(3)
            Log.e(javaClass.simpleName, "!! ResultFragment", )
        }

        Log.e(javaClass.simpleName, "time: $currentTime", )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //데이터 불러오기
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.runningData.observe(viewLifecycleOwner){datas ->
            if(datas.size>0){
                binding.time.text = datas.last().time
                binding.distance.text = datas.last().distance
                binding.calorie.text = datas.last().calorie
                binding.step.text = datas.last().step
            }

        }
    }
}