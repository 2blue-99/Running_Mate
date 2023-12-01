package com.running.runningmate2.utils

import android.location.Location
import androidx.lifecycle.MutableLiveData

class ListLiveData<T>: MutableLiveData<ArrayList<T>>(){
    init {
        value = arrayListOf()
    }

    fun set(item: T){
        value?.clear()
        value?.add(item)
    }

    fun add(item: T){
        val items = value
        if(items?.size!! > 2)
            items.removeFirst()
        items.add(item)
        value = items
    }

    fun size():Int = value?.size ?: 0

    fun getFirst():T = value?.first()!!
    fun getSecond():T = value?.last()!!
}