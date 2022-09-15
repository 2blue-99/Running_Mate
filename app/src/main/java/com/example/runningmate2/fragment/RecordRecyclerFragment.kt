package com.example.runningmate2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningmate2.DetailBottomsheet
import com.example.runningmate2.databinding.FragmentRecordRecyclerBinding
import com.example.runningmate2.recyclerView.Adapter
import com.example.runningmate2.recyclerView.Data
import com.example.runningmate2.recyclerView.toData
import com.example.runningmate2.viewModel.MainViewModel

class RecordRecyclerFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter = Adapter()
    val binding: FragmentRecordRecyclerBinding by lazy{
        FragmentRecordRecyclerBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.readDB()
        binding.myRecyclerView.apply {
            this.adapter = this@RecordRecyclerFragment.adapter
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        adapter.setItemClickListener(object: Adapter.OnItemClickListener{
            override fun onClick(data: Data) {
                val bottomDialogSheet = DetailBottomsheet(data)
                bottomDialogSheet.show(parentFragmentManager, bottomDialogSheet.tag)
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.runningData.observe(viewLifecycleOwner) { data ->
            if(data.size > 0){
                binding.myRecyclerView.minimumHeight = 875
                binding.noData.visibility=View.INVISIBLE
                Log.e("TAG", "데이터 넘어온거: $data", )
                adapter.datalist = data.map { it.toData() } as ArrayList<Data>

            }
            else{
                binding.myRecyclerView.minimumHeight = 875
                adapter.datalist = data.map { it.toData() } as ArrayList<Data>
                binding.noData.visibility=View.VISIBLE
                binding.noData.text = "달린 기록이 없어요.."
            }

        }
    }
}