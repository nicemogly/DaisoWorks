package com.example.daisoworks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataSujuDetail5
import com.example.daisoworks.databinding.HerpSuju5Binding

class ExpandableAdapterHerpSuju5(private var itemList: List<DataSujuDetail5>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpSuju5.ViewHolder>()  {


    inner class ViewHolder(val binding: HerpSuju5Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpSuju5Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){

                binding.tvcomname.text = this.comname
                binding.tvcomdept.text = this.comdept
                binding.tvownername.text = this.ownername
                binding.tvcomdepths.text = this.comdepths
                binding.tvownernamehs.text = this.ownernamehs



                binding.expandedHerpsujuView5.visibility = if (this.expand5) View.VISIBLE else View.GONE
                if(this.expand5) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand5 = !this.expand5
                    notifyDataSetChanged()
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList!!.size

}

