package com.example.runningmate2.fragment.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModelFragment : ViewModel(){
    fun repeatCallLocation(){
        viewModelScope.launch {
            // 여기서 위치 반복 호출 함수를 가져오고 메인스타트 프래그먼트에서
            // 뷰모델 호출해서 사용
        }
    }
}