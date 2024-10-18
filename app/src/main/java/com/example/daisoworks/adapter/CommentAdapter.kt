package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.DataDmsDetail3
import com.example.daisoworks.databinding.ItemRecycler1Binding


//import com.example.daisoworks.ui.slideshow.  .recyclerview.databinding.ItemRecyclerBinding

class CommentAdapterAdapter() : RecyclerView.Adapter<Holder1>(){

    var listData1 = mutableListOf<DataDmsDetail3>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder1 {
        val binding = ItemRecycler1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder1(binding)
    }

    override fun onBindViewHolder(holder1: Holder1, position: Int) {
        val listData1 = listData1[position]
        holder1.setData1(listData1)

    }



    override fun getItemCount(): Int {
        return listData1.size
    }
}

class Holder1(val binding: ItemRecycler1Binding) : RecyclerView.ViewHolder(binding.root){
    fun setData1(dataDmsDetail3: DataDmsDetail3){
       // binding.txtReq1.text = comment.reqNo
        binding.txtCmt2.text = dataDmsDetail3.fromEmpNm
        binding.txtCmt3.text = dataDmsDetail3.toEmpNm
        binding.txtCmt4.text = dataDmsDetail3.registDate
        binding.txtCmt5.text = dataDmsDetail3.cmnt
    }







}
