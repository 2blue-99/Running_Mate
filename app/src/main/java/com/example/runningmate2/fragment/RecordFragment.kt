package com.example.runningmate2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningmate2.RunningData
import com.example.runningmate2.databinding.FragmentRecordBinding

import com.example.runningmate2.recyclerView.Adapter
import com.example.runningmate2.recyclerView.Data
import com.example.runningmate2.recyclerView.toData
import com.example.runningmate2.viewModel.MainViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RecordFragment : Fragment() {

    val listAdapter = Adapter()
    private val mainViewModel: MainViewModel by activityViewModels()
    val binding: FragmentRecordBinding by lazy {
        FragmentRecordBinding.inflate(layoutInflater)
    }
    private val adapter = Adapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.readDB()
        binding.myRecyclerView.apply {
            this.adapter = this@RecordFragment.adapter
            this.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.deleteAllButton.setOnClickListener{
            viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                mainViewModel.deletAllDB()
            }
        }

        adapter.setItemClickListener(object: Adapter.OnItemClickListener{
            override fun onClick(position: Int) {
                val data = adapter.datalist[position]
                binding.deleteText.text = data.toString()
                Log.e(javaClass.simpleName, "fragment onClick: ${adapter.datalist[position]}")
                Log.e(javaClass.simpleName, "fragment onClick position: $position")
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    mainViewModel.deleteDB(data.id)
                    mainViewModel.readDB()
                }
            }
        })

        binding.deleteButton.setOnClickListener{
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                mainViewModel.deleteDB(9)
                mainViewModel.readDB()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.runningData.observe(viewLifecycleOwner) { data ->
            Log.e("TAG", "데이터 넘어온거: $data", )
            adapter.datalist = data.map { it.toData() } as ArrayList<Data>
        }
    }

}