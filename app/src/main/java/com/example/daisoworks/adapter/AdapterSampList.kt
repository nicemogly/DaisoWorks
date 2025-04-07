package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.DataSampleList
import com.example.daisoworks.databinding.SingleItem6Binding


class AdapterSampList(private var sampleList: ArrayList<DataSampleList>,private val listener: ItemClickListener ) : RecyclerView.Adapter<AdapterSampList.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: SingleItem6Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleItem6Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        with(holder){
            with(sampleList[position]){
//                binding.tvTclntConNo.text = this.tclntConNo
//                binding.tvExhDate.text = this.exhDate
                binding.tvSammgno.text = this.sammgno
                binding.tvSamnm.text = this.samnm
                binding.tvRcpdate.text = this.salercpdte

//                holder.itemView.setOnClickListener {
//                    itemClickListener.onClick(it, position)
//                }



                 binding.buttonDelete.setOnClickListener {
                     listener.onItemClicked(position)

                 }
            }
        }
    }



//
//    interface OnItemClickListener {
//        fun onClick(v: View, position: Int)
//    }
//    // (3) 외부에서 클릭 시 이벤트 설정
//    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
//        this.itemClickListener = onItemClickListener
//    }
//    // (4) setItemClickListener로 설정한 함수 실행
//    private lateinit var itemClickListener : OnItemClickListener
    override fun getItemCount(): Int = sampleList.size

}

