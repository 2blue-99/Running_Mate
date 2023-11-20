package com.running.runningmate2.base

import androidx.lifecycle.*
import kotlinx.coroutines.*

abstract class BaseViewModel(val name: String) : ViewModel(){

    protected val isLoading = MutableLiveData(false)

    private val job = SupervisorJob()

    protected val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
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