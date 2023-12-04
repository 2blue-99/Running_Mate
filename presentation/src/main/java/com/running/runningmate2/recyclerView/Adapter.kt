package com.running.runningmate2.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.running.domain.model.RunningData
import com.running.runningmate2.databinding.ItemRecordBinding

class Adapter: RecyclerView.Adapter<Adapter.MyViewHolder>() {

    var datalist = listOf<RunningData>()
    set(value) {
            field = value.reversed()
            notifyDataSetChanged()
        }

    private lateinit var binding: ItemRecordBinding
    private var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datalist[position],position)
        holder.itemView.setOnClickListener{
            itemClickListener?.onClick(datalist[position])
        }
    }

    inner class MyViewHolder(private val binding: ItemRecordBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RunningData, position:Int){
            binding.recordRecyclerItemTimeTxt.text= data.now
            binding.recordRecyclerItemNumberTxt.text= "No.${position + 1}"
        }
    }

    interface OnItemClickListener {
        fun onClick(data: RunningData)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}