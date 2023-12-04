package com.running.runningmate2.viewModel.activityViewModel

import com.running.runningmate2.base.BaseViewModel
import com.running.domain.model.RunningData
import com.running.domain.usecase.LocalDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localDataUseCase: LocalDataUseCase,
) : BaseViewModel() {

    var savedData: RunningData? = null
    fun insertDB(runningData: RunningData) {
        savedData = runningData
        ioScope.launch {
            localDataUseCase.insertData(runningData)
        }
    }
}