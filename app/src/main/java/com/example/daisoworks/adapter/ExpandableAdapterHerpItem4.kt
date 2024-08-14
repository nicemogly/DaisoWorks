package com.example.daisoworks.adapter

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataItemDetail4
import com.example.daisoworks.databinding.HerpItem4Binding

class ExpandableAdapterHerpItem4(private var itemList4: List<DataItemDetail4>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpItem4.ViewHolder>()  {

    inner class ViewHolder(val binding: HerpItem4Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpItem4Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList4.get(position)){
                binding.tvSampleNewItemNo.text = this.sampleNewItemNo
                binding.tvSampleNItemNo.text = this.sampleNItemNo
                binding.tvSampleCsNoteNo.text = this.sampleCsNoteNo
                binding.tvSampleCsNoteItemNo.text = this.sampleCsNoteItemNo
                binding.tvExhName.text = this.exhName
                binding.tvExhPeriod.text = this.exhPeriod
                if(this.exhDetail.isNullOrEmpty()) {
                    binding.tvExhDetail.text =
                        Editable.Factory.getInstance().newEditable(" ")
                }else{
                    binding.tvExhDetail.text =  Editable.Factory.getInstance().newEditable(this.exhDetail)
                }
                binding.expandedHerpitemView3.visibility = if (this.expand4) View.VISIBLE else View.GONE

                if(this.expand4) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand4 = !this.expand4
                    notifyDataSetChanged()
                }

            }
        }
    }

    override fun getItemCount(): Int = itemList4!!.size

}

