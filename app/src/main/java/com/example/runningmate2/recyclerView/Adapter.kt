package com.example.runningmate2.recyclerView

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.runningmate2.databinding.ItemListBinding
import com.example.runningmate2.fragment.RecordFragment
import java.time.LocalDateTime
import java.time.LocalTime

class Adapter: RecyclerView.Adapter<Adapter.MyViewHolder>() {

    var datalist = arrayListOf<Data>()//리사이클러뷰에서 사용할 데이터 미리 정의 -> 나중에 MainActivity등에서 datalist에 실제 데이터 추가
    @SuppressLint("NotifyDataSetChanged")
    set(value) {
            field = value
            notifyDataSetChanged()
        }

    private lateinit var binding: ItemListBinding
    private var itemClickListener : OnItemClickListener? = null

    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.e(javaClass.simpleName, "adapter onCreateViewHolder: ", )
        binding = ItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        recoderBinding = FragmentRecordBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    //recyclerview가 viewholder를 가져와 데이터 연결할때 호출
    //적절한 데이터를 가져와서 그 데이터를 사용하여 뷰홀더의 레이아웃 채움
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datalist[position])
        holder.itemView.setOnClickListener{
            itemClickListener?.onClick(position)
        }
    }

    inner class MyViewHolder(private val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root) {
        //원래라면 class MyViewHolder(itemView:View):RecyclerView.Viewholder(itemView)인데 바인딩으로 구현!
        //binding전달받았기 때문에 홀더 내부 어디에서나 binding 사용가능
//        private var view: View = v
//        fun bind(data:Data, listener: View.OnClickListener){
        fun bind(data:Data){
//            Log.e("TAG", "뷰홀더입니다.: $data", )
            binding.nowTime.text= data.now
            binding.recyclerTime.text= data.time
            binding.recyclerDistance.text= data.distance
            binding.recyclerCalorie.text= data.calorie
            binding.recyclerStep.text= data.step

        }
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행

}