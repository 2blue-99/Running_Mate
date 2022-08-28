package com.example.runningmate2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.runningmate2.MainActivity
import com.example.runningmate2.R
import com.example.runningmate2.databinding.FragmentMapsBinding
import com.example.runningmate2.databinding.FragmentResultBinding

class ResultFragment : Fragment() {
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

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
        return view
    }
}