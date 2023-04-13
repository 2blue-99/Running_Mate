package com.running.runningmate2.utils

import androidx.lifecycle.MutableLiveData

class ListLiveData<T>: MutableLiveData<ArrayList<T>>(){
    init {
        value = arrayListOf()
    }

    fun add(item: T){
        val items = value
        items?.add(item)
        value = items
    }

    fun addAll(itemList: List<T>){
        val items = value
        items?.addAll(itemList)
        value = items
    }

    fun clear() {
        val items = value
        items?.clear()
        value = items
    }
}