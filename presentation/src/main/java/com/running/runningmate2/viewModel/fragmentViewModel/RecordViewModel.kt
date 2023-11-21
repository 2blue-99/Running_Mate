package com.running.runningmate2.viewModel.fragmentViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.running.domain.model.RunningData
import com.running.domain.usecase.LocalDataUseCase
import com.running.runningmate2.base.BaseViewModel
import com.running.runningmate2.utils.ListLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 2023-11-21
 * pureum
 */
@HiltViewModel
class RecordViewModel @Inject constructor(
    private val roomUseCase: LocalDataUseCase
): BaseViewModel() {
    private val _runningData = MutableLiveData<List<RunningData>>()
    val runningData: LiveData<List<RunningData>> get() = _runningData

    fun readDB() {
        ioScope.launch {
            _runningData.postValue(roomUseCase.readAllData())
        }
    }

    fun deleteDB(data: RunningData) {
        ioScope.launch {
            roomUseCase.deleteData(data.id)
            readDB()
        }
    }
}