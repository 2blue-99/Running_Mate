package com.example.runningmate2.fragment.viewModel

import android.location.Location
import androidx.lifecycle.LiveData
import com.jaehyeon.locationpolylinetest.utils.ListLiveData

class RecordViewModel {
    //룸에서 데이터 가져와 리스트에 넣기.
    private val _userData = ListLiveData<Location>()
    val userData: LiveData<ArrayList<Location>> get() = _userData

    //룸 들어가서 가져오는 코드
    fun getData(){

    }
}