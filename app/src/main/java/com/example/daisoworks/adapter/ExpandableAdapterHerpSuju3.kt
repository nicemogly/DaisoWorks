package com.example.daisoworks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataSujuDetail3
import com.example.daisoworks.databinding.HerpSuju3Binding

class ExpandableAdapterHerpSuju3(private var itemList: List<DataSujuDetail3>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpSuju3.ViewHolder>()  {


    inner class ViewHolder(val binding: HerpSuju3Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpSuju3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){

                binding.tvbuyerinfo.text = this.buyerinfo
                binding.tvbuyercif.text = this.buyercif
                binding.tvbuyertax.text = this.buyertax



                binding.expandedHerpsujuView3.visibility = if (this.expand3) View.VISIBLE else View.GONE
                if(this.expand3) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand3 = !this.expand3
                    notifyDataSetChanged()
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList!!.size

}

