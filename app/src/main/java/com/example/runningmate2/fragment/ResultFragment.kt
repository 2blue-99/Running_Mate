package com.example.runningmate2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater,container, false)
        val view = binding.root
        binding.backBtn.setOnClickListener{
            (activity as MainActivity).changeFragment(3)
            Log.e(javaClass.simpleName, "!! ResultFragment", )
            mainViewModel.db.getDao().getData().observe(viewLifecycleOwner){datas ->
                Log.e(javaClass.simpleName, "room: $datas", )
            }
        }
        binding.time.text = mainViewModel.runningData.time
        binding.distance.text = mainViewModel.runningData.distance
        binding.calorie.text = mainViewModel.runningData.calorie
        binding.step.text = mainViewModel.runningData.step


        Log.e(javaClass.simpleName, "time: $currentTime", )

        return view
    }
}