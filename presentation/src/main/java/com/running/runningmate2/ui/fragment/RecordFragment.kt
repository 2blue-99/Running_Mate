package com.running.runningmate2.ui.fragment

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.running.domain.model.RunningData
import com.running.runningmate2.R
import com.running.runningmate2.base.BaseFragment
import com.running.runningmate2.databinding.FragmentRecordBinding
import com.running.runningmate2.recyclerView.Adapter
import com.running.runningmate2.utils.RunningBoxHelper
import com.running.runningmate2.viewModel.fragmentViewModel.RecordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecordFragment : BaseFragment<FragmentRecordBinding>(R.layout.fragment_record) {

    private val viewModel: RecordViewModel by viewModels()
    private val adapter by lazy { Adapter() }

    override fun initData() {
        viewModel.readDB()
    }

    override fun initUI() {
        parentFragmentManager
            .beginTransaction()
            .replace(binding.recordGraphFrame.id, RecordGraphFragment(viewModel))
            .commit()
    }

    override fun initListener() {
        binding.recordChangeBtn.setOnClickListener {
            if (binding.recordChangeBtn.text == "기록보기") {
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.recordRecyclerFrame.id, RecordRecyclerFragment(viewModel))
                    .commit()
                binding.recordChangeBtn.text = "통계보기"
                binding.recordRecyclerFrame.visibility = View.VISIBLE
                binding.recordGraphFrame.visibility = View.INVISIBLE
            } else {
                parentFragmentManager
                    .beginTransaction()
                    .replace(binding.recordGraphFrame.id, RecordGraphFragment(viewModel))
                    .commit()
                binding.recordChangeBtn.text = "기록보기"
                binding.recordRecyclerFrame.visibility = View.INVISIBLE
                binding.recordGraphFrame.visibility = View.VISIBLE
            }
        }
    }

    override fun initObserver() {
        viewModel.runningData.observe(viewLifecycleOwner) { dataList ->
            Log.e("TAG", "initObserver: $dataList", )
            adapter.datalist = dataList
            if (dataList.isNotEmpty())
                binding.recordRecordBox.data = RunningBoxHelper.makeRunningBox(dataList)
            else
                binding.recordRecordBox.data = RunningBoxHelper.makeFakeBox()
        }
    }
}