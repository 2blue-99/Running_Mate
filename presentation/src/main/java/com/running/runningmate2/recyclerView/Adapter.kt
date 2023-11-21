package com.running.runningmate2.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.running.domain.model.RunningData
import com.running.runningmate2.databinding.ItemListBinding

class Adapter: RecyclerView.Adapter<Adapter.MyViewHolder>() {

    var datalist = listOf<RunningData>()
    @SuppressLint("NotifyDataSetChanged")

    set(value) {
            field = value
            notifyDataSetChanged()
        }

    private lateinit var binding: ItemListBinding
    private var itemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
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

    inner class MyViewHolder(private val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RunningData, position:Int){
            binding.dateTxt.text= data.now
            binding.dayCountTxt.text= "No.${position + 1}"
        }
    }

    interface OnItemClickListener {
        fun onClick(data: RunningData)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
}