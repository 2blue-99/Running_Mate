package com.example.runningmate2

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.example.runningmate2.databinding.DialogBottomSheetBinding
import com.example.runningmate2.fragment.MainMapsFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet(
   val onClick: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var dlg : BottomSheetDialog
    private val binding: DialogBottomSheetBinding by lazy{
        DialogBottomSheetBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.includedWeight.bottomTitle.text = "체중"
        binding.includedDistance.bottomTitle.text = "목표 거리"
        binding.includedStep.bottomTitle.text = "목표 걸음"

        binding.BottomStartButton.setOnClickListener{
            if(binding.includedWeight.inputEdit.text.isNotEmpty()){
                onClick()
                dismiss()
                return@setOnClickListener
            }
            Toast.makeText(context, "몸무게는 필수 항목입니다.", Toast.LENGTH_SHORT).show()
        }

        binding.includedWeight.inputData.setOnClickListener{
            binding.includedWeight.inputEdit.setTextColor(Color.GRAY)
        }
        binding.includedDistance.inputData.setOnClickListener{
            binding.includedDistance.inputEdit.setTextColor(Color.GRAY)
        }
        binding.includedStep.inputData.setOnClickListener{
            binding.includedStep.inputEdit.setTextColor(Color.GRAY)
        }

        binding.includedWeight.inputEdit.setOnClickListener{
            binding.includedWeight.inputEdit.setTextColor(Color.WHITE)
        }
        binding.includedDistance.inputEdit.setOnClickListener{
            binding.includedDistance.inputEdit.setTextColor(Color.WHITE)
        }
        binding.includedStep.inputEdit.setOnClickListener{
            binding.includedStep.inputEdit.setTextColor(Color.WHITE)
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