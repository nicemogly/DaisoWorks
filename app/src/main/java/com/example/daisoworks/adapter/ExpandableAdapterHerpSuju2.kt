package com.example.daisoworks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataSujuDetail2
import com.example.daisoworks.databinding.HerpSuju2Binding

class ExpandableAdapterHerpSuju2(private var itemList: List<DataSujuDetail2>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpSuju2.ViewHolder>()  {


    inner class ViewHolder(val binding: HerpSuju2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpSuju2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){

                binding.tvsujustnation.text = this.sujustnation
                binding.tvsujustcity.text = this.sujustcity
                binding.tvsujustcenter.text = this.sujustcenter
                binding.tvsujuednation.text = this.sujuednation
                binding.tvsujuedcity.text = this.sujuedcity
                binding.tvsujuedcenter.text = this.sujuedcenter



                binding.expandedHerpsujuView2.visibility = if (this.expand2) View.VISIBLE else View.GONE
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

    override fun getItemCount(): Int = itemList!!.size

}

