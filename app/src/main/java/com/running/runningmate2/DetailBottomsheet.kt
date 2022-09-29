package com.running.runningmate2

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.running.runningmate2.databinding.DialogBinding
import com.running.runningmate2.recyclerView.Data
import com.running.runningmate2.viewModel.MainViewModel

class DetailBottomsheet(
    val data: Data
) : BottomSheetDialogFragment(){
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var dlg : BottomSheetDialog
    private val binding: DialogBinding by lazy {
        DialogBinding.inflate(layoutInflater)
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
                mainViewModel.deleteDB(data)
                mainViewModel.readDB()
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
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 만들어놓은 dialog_profile.xml 뷰를 띄운다.
//        setContentView(binding.root)
//        initViews()
//    }
//
//    private fun initViews() = with(binding) {
//        // 뒤로가기 버튼, 빈 화면 터치를 통해 dialog가 사라지지 않도록
////        setCancelable(false)
//
//        // background를 투명하게 만듦
//        // (중요) Dialog는 내부적으로 뒤에 흰 사각형 배경이 존재하므로, 배경을 투명하게 만들지 않으면
//        // corner radius의 적용이 보이지 않는다.
////        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        // OK Button 클릭에 대한 Callback 처리. 이 부분은 상황에 따라 자유롭게!
//
//        binding.dialogBackBtn.setOnClickListener {
//            dismiss()
//        }
//        binding.dialogRemoveBtn.setOnClickListener {
//            Log.e("TAG", "삭제 : 삭제입니다. ",)
////            lifecycleScope.launchWhenCreated {
////                mainViewModel.deleteDB(adapter.datalist[position].id)
////                mainViewModel.readDB()
////            }
//        }
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        dlg = (super.onCreateDialog(savedInstanceState).apply {
//            setOnShowListener{
//                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
//                bottomSheet.setBackgroundResource(android.R.color.transparent)
//            }
//        }) as BottomSheetDialog
//        return dlg
//    }
}