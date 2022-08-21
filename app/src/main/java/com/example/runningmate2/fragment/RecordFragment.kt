package com.example.runningmate2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runningmate2.R

import com.example.runningmate2.recyclerView.Adapter
import com.example.runningmate2.recyclerView.Data

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RecordFragment : Fragment() {
    val mDatas=mutableListOf<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e(javaClass.simpleName, "recordFragment start: ", )

        //recyclerView 초기값 넣기
        InitRecylcerViewData()

//        recordBinding = FragmentRecordBinding.inflate(layoutInflater)
        val recordBinding = inflater.inflate(R.layout.fragment_record, container, false)
        val myRecylcer : RecyclerView = recordBinding.findViewById(R.id.myRecyclerView)
        val adapter = Adapter()
        adapter.datalist = mDatas
        myRecylcer.adapter = adapter
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_record, container, false)
        return recordBinding
    }

    fun InitRecylcerViewData() {
        mDatas.add(Data("aaaa", "bb", "cc"))
        mDatas.add(Data("aaaa", "bb", "cc"))
        mDatas.add(Data("aaaa", "bb", "cc"))
        mDatas.add(Data("aaaa", "bb", "cc"))
        mDatas.add(Data("aaaa", "bb", "cc"))
        mDatas.add(Data("aaaa", "bb", "cc"))
        }
    }