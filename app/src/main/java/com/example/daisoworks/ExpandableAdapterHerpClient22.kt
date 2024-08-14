package com.example.daisoworks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.DataClientDetail22
import com.example.daisoworks.databinding.HerpClient21Binding


class ExpandableAdapterHerpClient22(val context: Context, private var clientList2: List<DataClientDetail22>) : RecyclerView.Adapter<ExpandableAdapterHerpClient22.ViewHolder>()  {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: HerpClient21Binding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: DataClientDetail22) {
            binding.tvClientItemNo.text = item.clientItemNo
            binding.tvClientItemName.text = item.clientItemName

        }

      /*  fun bind(item: RecyclerInViewModel) {
            binding.model = item
        }
        */

    }

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpClient21Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    /*clientNoP,clientPreNoP,clientBizNoP,clientBizNameK,clientBizAddrK,clientBizCeoK,clientBizNameE,clientBizAddrE,clientBizCeoE,
       clientBizNameC,clientBizAddrC,clientBizCeoC,clientBizCountry,clientBizKind,clientBizTel,clientBizHomepage,clientBizEmail,expand1*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        with(holder){
            with(clientList2[position]){

                Log.d("testtest" , "{$position}")
                holder.bind(clientList2[position])
                // set name of the language from the list
                //상품정보
        /*        var aaa:String =  this.clientItemNo

                 binding.tvClientItemNo.text = this.clientItemNo
                 binding.tvClientItemName.text = this.clientItemName*/
            }
        }
    }

    override fun getItemCount(): Int = clientList2.size

}