package com.running.runningmate2.base

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*

abstract class BaseViewModel : ViewModel(){

    protected val isLoading = MutableLiveData(false)

    private val job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        isLoading.postValue(false)
        coroutineContext.job.cancel()
    }

    protected val modelScope = viewModelScope + job + exceptionHandler
    protected val ioScope = CoroutineScope(Dispatchers.IO) + job + exceptionHandler

    override fun onCleared() {
        super.onCleared()
        if (!job.isCancelled || !job.isCompleted)
            job.cancel()
    }
}