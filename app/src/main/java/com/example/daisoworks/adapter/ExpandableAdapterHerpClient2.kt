package com.example.daisoworks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataClientDetail2
import com.example.daisoworks.databinding.HerpClient2Binding


class ExpandableAdapterHerpClient2(val context: Context, private var clientList1: List<DataClientDetail2> ) : RecyclerView.Adapter<ExpandableAdapterHerpClient2.ViewHolder>()  {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: HerpClient2Binding) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: DataClientDetail2) {


         //   binding.model = item

             binding.rvHerpClientlist21.adapter = ExpandableAdapterHerpClient22(context , item.innerList)
             binding.rvHerpClientlist21.layoutManager = LinearLayoutManager(context)
        }
    }

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpClient2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    /*clientNoP,clientPreNoP,clientBizNoP,clientBizNameK,clientBizAddrK,clientBizCeoK,clientBizNameE,clientBizAddrE,clientBizCeoE,
       clientBizNameC,clientBizAddrC,clientBizCeoC,clientBizCountry,clientBizKind,clientBizTel,clientBizHomepage,clientBizEmail,expand1*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = clientList1[position]
        holder.bind(item)


        with(holder){
            with(clientList1[position]){
                // set name of the language from the list
                //상품정보
               // binding.tvClientItemNo.text = this.clientItemNo
             //   binding.tvClientItemName.text = this.clientItemName

                binding.expandedHerpclientView1.visibility = if (this.expand2) View.VISIBLE else View.GONE


                if(this.expand2) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand2 = !this.expand2
                    notifyDataSetChanged()
                }




            }
        }
    }

    override fun getItemCount(): Int = clientList1.size

}