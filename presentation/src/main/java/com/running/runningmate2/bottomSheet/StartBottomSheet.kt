package com.running.runningmate2.bottomSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.running.runningmate2.databinding.BottomSheetStartBinding

class StartBottomSheet(
    val weight:Int,
   val onClick: (String) -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var dlg : BottomSheetDialog
    private val binding: BottomSheetStartBinding by lazy{
        BottomSheetStartBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.startBottomSheetWeightBox.inputTitleTxt.text = "체중"
        binding.startBottomSheetWeightBox.inputTypeTxt.text = "Kg"

        binding.startBottomSheetDistenceBox.inputTitleTxt.text = "목표 거리"
        binding.startBottomSheetDistenceBox.inputTypeTxt.text = "Km"

        binding.startBottomSheetStepBox.inputTitleTxt.text = "목표 걸음"
        binding.startBottomSheetStepBox.inputTypeTxt.text = "걸음"

        if(weight != 0) binding.startBottomSheetWeightBox.inputEditTxt.setText("$weight")

        binding.mapsStartBottomSheetStartBtn.setOnClickListener{
            if(binding.startBottomSheetWeightBox.inputEditTxt.text.isNotEmpty()){
                onClick(binding.startBottomSheetWeightBox.inputEditTxt.text.toString())
                dismiss()
                return@setOnClickListener
            }
            Toast.makeText(context, "몸무게는 필수 항목입니다.", Toast.LENGTH_SHORT).show()
        }
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