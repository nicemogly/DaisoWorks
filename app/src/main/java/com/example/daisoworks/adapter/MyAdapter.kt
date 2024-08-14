package com.example.daisoworks.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.SubActivity
import com.example.daisoworks.data.Member
import com.example.daisoworks.databinding.ItemRecyclerBinding


//import com.example.daisoworks.ui.slideshow.  .recyclerview.databinding.ItemRecyclerBinding

class MyAdapter() : RecyclerView.Adapter<Holder>(){

    var listData = mutableListOf<Member>()

/*    var listData1 = mutableListOf<Comment>()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val member = listData[position]
        holder.setData(member)


        holder.binding.fab.setOnClickListener {
            val intent = Intent(holder.itemView?.context, SubActivity::class.java)
            intent.putExtra("reqNo" , "${member.reqNo}")
            intent.putExtra("reqItemDesc" , "${member.reqItemDesc}")
            ContextCompat.startActivity(holder.binding.root.context, intent, null)

           // Log.d("PUTVALUE전" , "{$member.reqNo}")

        }

    }



    override fun getItemCount(): Int {
        return listData.size
    }
}

class Holder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root){
    fun setData(member: Member){
        binding.txtReq1.text = member.reqNo
        binding.txtReq2.text = member.reqItemNo
        binding.txtReq3.text = member.reqItemDesc
        binding.txtReq4.text = member.reqDeowner
        binding.txtReq5.text = member.reqValday
    }




/*
    fun setData1(comment: Comment){
        binding.txtCmt2.text = comment.reqSendName
        binding.txtCmt3.text = comment.reqRedvName
        binding.txtCmt4.text = comment.reqDate
        binding.txtCmt5.text = comment.reqConts
    }
*/




    init {
        binding.fab.setOnClickListener {
            // itemClickListener?.onItemClick(adapterPosition)


            Log.d("fab CLick" , "$adapterPosition")
        }
    }


}
