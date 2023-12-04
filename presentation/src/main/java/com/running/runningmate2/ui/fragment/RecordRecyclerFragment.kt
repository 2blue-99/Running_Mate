package com.running.runningmate2.ui.fragment

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.running.domain.model.RunningData
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.bottomSheet.DetailBottomSheet
import com.running.runningmate2.databinding.FragmentRecordRecyclerBinding
import com.running.runningmate2.recyclerView.Adapter
import com.running.runningmate2.viewModel.fragmentViewModel.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecordRecyclerFragment(
    recordViewModel: RecordViewModel
) : BaseFragment<FragmentRecordRecyclerBinding>(R.layout.fragment_record_recycler) {
    private val viewModel = recordViewModel
    private val adapter by lazy { Adapter() }

    override fun initData() {
        viewModel.readDB()
    }

    override fun initUI() {
        binding.recordRecyclerView.apply {
            this.adapter = this@RecordRecyclerFragment.adapter
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initListener() {
        adapter.setItemClickListener(object: Adapter.OnItemClickListener{
            override fun onClick(data: RunningData) {
                val bottomDialogSheet = DetailBottomSheet(data, viewModel)
                bottomDialogSheet.show(parentFragmentManager, bottomDialogSheet.tag)
            }
        })
    }

    override fun initObserver() {
        viewModel.runningData.observe(viewLifecycleOwner) { data ->
            if(data.isNotEmpty()){
                binding.recordRecyclerView.minimumHeight = 875
                binding.recordRecyclerEmptyTxt.visibility=View.INVISIBLE
                adapter.datalist = data

            }
            else{
                binding.recyclerConstrain.setBackgroundColor(Color.parseColor("#4DFFFFFF"))
                binding.recordRecyclerView.minimumHeight = 875
                adapter.datalist = data
                binding.recordRecyclerEmptyTxt.visibility=View.VISIBLE
                binding.recordRecyclerEmptyTxt.text = "기록 데이터가 없어요.."
            }
        }
    }
}