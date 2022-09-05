package com.example.runningmate2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningmate2.R
import com.example.runningmate2.databinding.FragmentRecordBinding

import com.example.runningmate2.recyclerView.Adapter
import com.example.runningmate2.recyclerView.Data
import com.example.runningmate2.recyclerView.toData
import com.example.runningmate2.viewModel.MainViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RecordFragment : Fragment() {
//    val mDatas = mutableListOf<Data>()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding: FragmentRecordBinding by lazy {
        FragmentRecordBinding.inflate(layoutInflater)
    }
    private val adapter = Adapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.getData()
        binding.myRecyclerView.apply {
            this.adapter = this@RecordFragment.adapter
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.runningData.observe(viewLifecycleOwner) { data ->
            adapter.datalist = data.map { it.toData() } as ArrayList<Data>
        }
    }

    fun init() {
        // 룸에서 가져오는 코드.
        mainViewModel.runningData.observe(viewLifecycleOwner) { data ->
            if (data.size > 0) {




//                for(datas in data){
//                    mDatas.add(Data(
//                        datas.time,
//                        datas.distance,
//                        datas.calorie,
//                        datas.step))
//                }
            }
        }
    }
}