package com.running.runningmate2.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.running.runningmate2.DetailBottomsheet
import com.running.runningmate2.databinding.FragmentRecordRecyclerBinding
import com.running.runningmate2.recyclerView.Adapter
import com.running.runningmate2.recyclerView.Data
import com.running.runningmate2.recyclerView.toData
import com.running.runningmate2.viewModel.MainViewModel

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
                adapter.datalist = data.map { it.toData() } as ArrayList<Data>

            }
            else{
                binding.recyclerConstrain.setBackgroundColor(Color.parseColor("#4DFFFFFF"))
                binding.myRecyclerView.minimumHeight = 875
                adapter.datalist = data.map { it.toData() } as ArrayList<Data>
                binding.noData.visibility=View.VISIBLE
                binding.noData.text = "기록 데이터가 없어요.."
            }

        }
    }
}