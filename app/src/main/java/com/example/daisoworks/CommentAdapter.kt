package com.example.daisoworks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.databinding.ItemRecycler1Binding


//import com.example.daisoworks.ui.slideshow.  .recyclerview.databinding.ItemRecyclerBinding

class CommentAdapterAdapter() : RecyclerView.Adapter<Holder1>(){

    var listData1 = mutableListOf<Comment>()

    /*    var listData1 = mutableListOf<Comment>()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder1 {
        val binding = ItemRecycler1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder1(binding)
    }

    override fun onBindViewHolder(holder1: Holder1, position: Int) {
        val comment = listData1[position]
        holder1.setData1(comment)

    }



    override fun getItemCount(): Int {
        return listData1.size
    }
}

class Holder1(val binding: ItemRecycler1Binding) : RecyclerView.ViewHolder(binding.root){
    fun setData1(comment: Comment){
       // binding.txtReq1.text = comment.reqNo
        binding.txtCmt2.text = comment.reqSendName
        binding.txtCmt3.text = comment.reqRedvName
        binding.txtCmt4.text = comment.reqDate
        binding.txtCmt5.text = comment.reqConts
    }







}
