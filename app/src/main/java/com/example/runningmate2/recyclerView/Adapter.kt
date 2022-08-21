package com.example.runningmate2.recyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runningmate2.databinding.ItemListBinding

class Adapter: RecyclerView.Adapter<Adapter.MyViewHolder>() {

    var datalist = mutableListOf<Data>()//리사이클러뷰에서 사용할 데이터 미리 정의 -> 나중에 MainActivity등에서 datalist에 실제 데이터 추가

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.e(javaClass.simpleName, "adapter onCreateViewHolder: ", )
        val binding= ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.e(javaClass.simpleName, "adapter getItemCount: ", )
        return datalist.size
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d(javaClass.simpleName, "holder : $holder ")
        holder.bind(datalist[position])
        val listener = View.OnClickListener {
            Toast.makeText(it.context, "hello", Toast.LENGTH_SHORT).show()
        }

    }

    inner class MyViewHolder(private val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root) {
        //원래라면 class MyViewHolder(itemView:View):RecyclerView.Viewholder(itemView)인데 바인딩으로 구현!
        //binding전달받았기 때문에 홀더 내부 어디에서나 binding 사용가능
//        private var view: View = v
//        fun bind(data:Data, listener: View.OnClickListener){
        fun bind(data:Data){
            Log.e(javaClass.simpleName, "adapter MyViewHolder: ", )
            //binding.dogPhotoImg.=dogData.dog_img
            binding.textView.text= data.A
            binding.textView2.text= data.B
            binding.textView3.text= data.C
//            view.setOnClickListener(listener)
        }
    }
}