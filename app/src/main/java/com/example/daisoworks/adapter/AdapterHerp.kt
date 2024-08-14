package com.example.daisoworks.adapter


import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.SujuActivity
import com.example.daisoworks.data.HerpSujuL
import com.example.daisoworks.databinding.ItemRecyclerHerpBinding


//import com.example.daisoworks.ui.slideshow.  .recyclerview.databinding.ItemRecyclerBinding

class AdapterHerp() : RecyclerView.Adapter<HolderHerp>(){

    var listData = mutableListOf<HerpSujuL>()

    /*    var listData1 = mutableListOf<Comment>()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderHerp {
        val binding = ItemRecyclerHerpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HolderHerp(binding)
    }

    override fun onBindViewHolder(holder: HolderHerp, position: Int) {
        val sujuList = listData[position]
        holder.setData(sujuList)

        //data class HerpSujuL(val sujuBuyer: String , val sujuNo: String  , val itemNo: String  , val itemDesc: String  , val sujuDate: String , val sujuInt: Int , val sujuUnit : String)

        holder.binding.fab.setOnClickListener {
            val intent = Intent(holder.itemView?.context, SujuActivity::class.java)
            intent.putExtra("sujuNo" , "${sujuList.sujuNo}")
            intent.putExtra("itemNo" , "${sujuList.itemNo}")
            intent.putExtra("sujuBuyer" , "${sujuList.sujuBuyer}")
            ContextCompat.startActivity(holder.binding.root.context, intent, null)



        }

    }



    override fun getItemCount(): Int {
        return listData.size
    }
}

class HolderHerp(val binding: ItemRecyclerHerpBinding) : RecyclerView.ViewHolder(binding.root){
    fun setData(herpsujul: HerpSujuL){
        binding.txtsujuBuyer.text = herpsujul.sujuBuyer
        if(herpsujul.sujuBuyer=="아성다이소"){
            binding.txtsujuBuyer.setBackgroundColor(Color.parseColor("#9AB9FF"))
        }else{
            binding.txtsujuBuyer.setBackgroundColor(Color.parseColor("#80763A"))
        }
        binding.txtsujuNo.text = herpsujul.sujuNo
        binding.txtitemNo.text = herpsujul.itemNo
        binding.txtitemDesc.text = herpsujul.itemDesc
        binding.txtsujuDate.text = herpsujul.sujuDate
        binding.txtsujuInt.text = herpsujul.sujuInt.toString() +  herpsujul.sujuUnit.toString()
    /*    binding.txtsujuUnit.text = herpsujul.sujuUnit.toString()*/


    }


    init {
        binding.fab.setOnClickListener {
            // itemClickListener?.onItemClick(adapterPosition)
            Log.d("fab CLick" , "$adapterPosition")
        }
    }


}
