package com.running.runningmate2.ui.fragment

import android.graphics.Color
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.bottomSheet.DetailBottomsheet
import com.running.runningmate2.databinding.FragmentRecordRecyclerBinding
import com.running.runningmate2.recyclerView.Adapter
import com.running.runningmate2.model.Data
import com.running.runningmate2.model.toData
import com.running.runningmate2.viewModel.activityViewModel.MainViewModel

class RecordRecyclerFragment : BaseFragment<FragmentRecordRecyclerBinding>(R.layout.fragment_record_recycler) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter by lazy { Adapter() }

    override fun initData() {
        mainViewModel.readDB()
    }

    override fun initUI() {
        binding.myRecyclerView.apply {
            this.adapter = this@RecordRecyclerFragment.adapter
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initListener() {
        adapter.setItemClickListener(object: Adapter.OnItemClickListener{
            override fun onClick(data: Data) {
                val bottomDialogSheet = DetailBottomsheet(data)
                bottomDialogSheet.show(parentFragmentManager, bottomDialogSheet.tag)
            }
        })
    }

    override fun initObserver() {
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