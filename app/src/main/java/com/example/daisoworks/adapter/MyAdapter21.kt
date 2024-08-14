package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.DataClientDetail22
import com.example.daisoworks.databinding.HerpClient21Binding


class MyAdapter21() : RecyclerView.Adapter<Holder21>(){

    var listData1 = mutableListOf<DataClientDetail22>()

    /*    var listData1 = mutableListOf<Comment>()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder21 {
        val binding = HerpClient21Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder21(binding)
    }

    override fun onBindViewHolder(holder21: Holder21, position: Int) {
        val client1 = listData1[position]
        holder21.setData(client1)

    }



    override fun getItemCount(): Int {
        return listData1.size
    }
}

class Holder21(val binding: HerpClient21Binding) : RecyclerView.ViewHolder(binding.root){
    fun setData(client: DataClientDetail22){
        binding.tvClientItemNo.text = client.clientItemNo
        binding.tvClientItemName.text = client.clientItemName

    }




    }



