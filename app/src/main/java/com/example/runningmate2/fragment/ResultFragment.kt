package com.example.runningmate2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.runningmate2.MainActivity
import com.example.runningmate2.R
import com.example.runningmate2.databinding.FragmentMapsBinding
import com.example.runningmate2.databinding.FragmentResultBinding
import com.example.runningmate2.viewModel.MainViewModel

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater,container, false)
        val view = binding.root
        binding.button2.setOnClickListener{
            (activity as MainActivity).changeFragment(3)
        }
        binding.textView2.text = mainViewModel.pureum

        return view
    }
}