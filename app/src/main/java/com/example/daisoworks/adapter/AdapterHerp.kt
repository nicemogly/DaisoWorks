package com.example.daisoworks.adapter


import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.SujuActivity
import com.example.daisoworks.data.DataSujuDetail0
import com.example.daisoworks.databinding.ItemRecyclerHerpBinding


//import com.example.daisoworks.ui.slideshow.  .recyclerview.databinding.ItemRecyclerBinding

class AdapterHerp() : RecyclerView.Adapter<HolderHerp>(){

    var listData = mutableListOf<DataSujuDetail0>()

    /*    var listData1 = mutableListOf<Comment>()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderHerp {
        val binding = ItemRecyclerHerpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HolderHerp(binding)
    }

    override fun onBindViewHolder(holder: HolderHerp, position: Int) {
        val sujuList = listData[position]
        Log.d("DataSujuDetail0", "2")

        holder.setData(sujuList)

        //data class HerpSujuL(val sujuBuyer: String , val sujuNo: String  , val itemNo: String  , val itemDesc: String  , val sujuDate: String , val sujuInt: Int , val sujuUnit : String)

        holder.binding.fab.setOnClickListener {


            val comnov1 ="${sujuList.sujuno}"
            val comnov2 ="${sujuList.gdsno}"
            val comnov3 ="${sujuList.buycorpcd}"
            val comnov4 ="${sujuList.buygdsbcd}"
//            val action =  HomeFragmentDirections.actionNavHomeToNavSuju( comno1 = "$comnov1" ,comno2 = "$comnov2",comno3 = "$comnov3" , comno4 = "$comnov4")
//
//
//                Navigation.findNavController(it).navigate(action)



            val intent = Intent(holder.itemView?.context, SujuActivity::class.java)
            intent.putExtra("sujumginitno" , "${sujuList.sujumginitno}")
            intent.putExtra("sujuNo" , "${sujuList.sujuno}")
            intent.putExtra("itemNo" , "${sujuList.gdsno}")
            intent.putExtra("sujuBuyer" , "${sujuList.buycorpcd}")
            intent.putExtra("buygdsbcd" , "${sujuList.buygdsbcd}")
            intent.putExtra("gdsname" , "${sujuList.gdsnmekor}")
            ContextCompat.startActivity(holder.binding.root.context, intent, null)

        }

    }



    override fun getItemCount(): Int {
        return listData.size
    }
}

class HolderHerp(val binding: ItemRecyclerHerpBinding) : RecyclerView.ViewHolder(binding.root){
    fun setData(herpsujul: DataSujuDetail0){

        Log.d("DataSujuDetail0", "2")

        var kcomcha : String = ""
        if(herpsujul.buycorpcd == "10000") {
            kcomcha = "아성HMP"
        }else if(herpsujul.buycorpcd == "10005") {
            kcomcha = "(주)아성다이소"
        }else if(herpsujul.buycorpcd == "30510") {
            kcomcha = "아성솔루션"
        }else if(herpsujul.buycorpcd == "10001") {
            kcomcha = "DAISO INDUSTRIES CO., LTD."
        }else if(herpsujul.buycorpcd == "12004") {
            kcomcha = "(주)다이소출판"
        }else if(herpsujul.buycorpcd == "12002") {
            kcomcha = "PLUS ONE CO.,LTD"
        }else if(herpsujul.buycorpcd == "12000") {
            kcomcha = "JC SALES"
        }else if(herpsujul.buycorpcd == "12003") {
            kcomcha = "IAC Commerce Inc."
        }

        binding.txtsujuBuyer.text = kcomcha
        if(herpsujul.buycorpcd=="10005"){
            binding.txtsujuBuyer.setBackgroundColor(Color.parseColor("#9AB9FF"))
        }else{
            binding.txtsujuBuyer.setBackgroundColor(Color.parseColor("#80763A"))
        }
        binding.txtsujuNo.text = herpsujul.sujumginitno
        binding.txtitemNo.text = herpsujul.gdsno
        binding.txtitemDesc.text = herpsujul.gdsnmekor
        binding.txtsujuDate.text = herpsujul.sujudate
        binding.txtsujuInt.text = herpsujul.sujuindiqty
    /*    binding.txtsujuUnit.text = herpsujul.sujuUnit.toString()*/




    }


    init {
        binding.fab.setOnClickListener {
            // itemClickListener?.onItemClick(adapterPosition)
            Log.d("fab CLick" , "$adapterPosition")
        }
    }


}
