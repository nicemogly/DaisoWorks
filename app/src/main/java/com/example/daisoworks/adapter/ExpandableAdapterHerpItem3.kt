package com.example.daisoworks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataItemDetail3
import com.example.daisoworks.databinding.HerpItem3Binding

class ExpandableAdapterHerpItem3(private var itemList3: List<DataItemDetail3>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpItem3.ViewHolder>()  {

    inner class ViewHolder(val binding: HerpItem3Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpItem3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList3.get(position)){
                binding.tvOwnerDeptName.text = this.ownerDeptName
                binding.tvOwnerName.text = this.ownerName
                binding.tvOwnerCompany.text = this.ownerCompany
                binding.tvBusinessOwnerDept.text = this.businessOwnerDept
                binding.tvBusinessOwnerName.text = this.businessOwnerName
                binding.tvShipmentDept.text = this.shipmentDept
                binding.tvShipmentOwnerName.text = this.shipmentOwnerName
                binding.expandedHerpitemView2.visibility = if (this.expand3) View.VISIBLE else View.GONE

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

    override fun getItemCount(): Int = itemList3!!.size

}

