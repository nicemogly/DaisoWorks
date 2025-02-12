package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.ExhibitionMyList
import com.example.daisoworks.databinding.SingleItem5Binding


class ExpandableAdapterMyList(private var languageList: ArrayList<ExhibitionMyList>) : RecyclerView.Adapter<ExpandableAdapterMyList.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: SingleItem5Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleItem5Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        with(holder){
            with(languageList[position]){
//                binding.tvTclntConNo.text = this.tclntConNo
//                binding.tvExhDate.text = this.exhDate
                binding.tvExhComName.text = this.exhComName
                binding.tvExhNum.text = this.exhNum

                holder.itemView.setOnClickListener {
                    itemClickListener.onClick(it, position)
                }

            }
        }
    }
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener
    override fun getItemCount(): Int = languageList.size

}

