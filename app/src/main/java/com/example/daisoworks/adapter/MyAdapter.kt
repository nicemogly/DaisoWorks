package com.example.daisoworks.adapter

import android.content.Intent
import android.graphics.Color
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
            intent.putExtra("reqNo" , "${member.reqId}")
            intent.putExtra("revNo" , "${member.revNo}")
            intent.putExtra("reqItemDesc" , "${member.korProductDesc}")
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
        binding.txtReq1.text = member.reqId
        binding.txtReq2.text = member.productCd
        binding.txtReq3.text = member.korProductDesc
        binding.txtReq4.text = member.mainDsnEmpNm
        binding.txtReq5.text = member.cmplExptDate

       if(member.apprStts == "RQ") {
           binding.txtReq3.setBackgroundColor(Color.parseColor("#448AFF"))
       }else if (member.apprStts == "CM") {
           binding.txtReq3.setBackgroundColor(Color.parseColor("#C6C2C2"))
       }

    }







    init {
        binding.fab.setOnClickListener {
            // itemClickListener?.onItemClick(adapterPosition)


            Log.d("fab CLick" , "$adapterPosition")
        }
    }


}
