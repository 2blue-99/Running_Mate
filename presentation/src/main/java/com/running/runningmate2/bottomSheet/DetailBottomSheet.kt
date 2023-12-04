package com.running.runningmate2.bottomSheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.running.domain.model.RunningData
import com.running.runningmate2.databinding.BottomSheetRecordBinding
import com.running.runningmate2.viewModel.fragmentViewModel.RecordViewModel

class DetailBottomSheet(
    val data: RunningData,
    recordViewModel: RecordViewModel
) : BottomSheetDialogFragment(){
    private val viewModel = recordViewModel
    private lateinit var dlg : BottomSheetDialog
    private val binding: BottomSheetRecordBinding by lazy {
        BottomSheetRecordBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.dialogBackBtn.setOnClickListener{
            dismiss()
        }

        binding.dialogRemoveBtn.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                viewModel.deleteDB(data)
            }
            dismiss()
        }
        binding.detailDate.text = "${data.now.split(" ")[0].split("-")[0]}년 ${data.now.split(" ")[0].split("-")[1]}월 ${data.now.split(" ")[0].split("-")[2]}일 \n" +
                "${data.now.split(" ")[1].split(":")[0]}시 ${data.now.split(" ")[1].split(":")[1]}분 런닝"
        binding.detailTime.text = data.time
        binding.detailDistance.text = data.distance
        binding.detailCalorie.text = data.calorie
        binding.detailStep.text = data.step
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dlg = (super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener{
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }) as BottomSheetDialog
        return dlg
    }
}