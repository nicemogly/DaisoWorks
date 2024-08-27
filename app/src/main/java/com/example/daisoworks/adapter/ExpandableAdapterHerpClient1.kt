package com.example.daisoworks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataClientDetail1
import com.example.daisoworks.databinding.HerpClient1Binding

class ExpandableAdapterHerpClient1(private var itemList: List<DataClientDetail1> ,   val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpClient1.ViewHolder>()  {

    inner class ViewHolder(val binding: HerpClient1Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpClient1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){
                // set name of the language from the list
                //상품정보
                binding.tvClientNoP.text = this.clientNoP
                binding.tvClientPreNoP .text = this.clientPreNoP
                binding.tvClientBizNum.text = this.clientBizNoP
                binding.tvClientBizMNum.text = this.clientBizMNoP
                binding.tvClientBizNameK.text = this.clientBizNameK
                binding.tvClientBizAddrK.text = this.clientBizAddrK
                binding.tvClientBizCeoK.text = this.clientBizCeoK
                binding.tvClientBizNameE.text = this.clientBizNameE
                binding.tvClientBizAddrE.text = this.clientBizAddrE
                binding.tvClientBizCeoE.text = this.clientBizCeoE
                binding.tvClientBizNameC.text = this.clientBizNameC
                binding.tvClientBizAddrC.text = this.clientBizAddrC
                binding.tvClientBizCeoC.text = this.clientBizCeoC
                binding.tvClientBizCountry.text = this.clientBizCountry
                binding.tvClientBizKind.text = this.clientBizKind
                binding.tvClientBizTel.text = this.clientBizTel
                binding.tvClientBizHomepage.text = this.clientBizHomepage
                binding.tvClientBizEmail.text = this.clientBizEmail

                binding.expandedHerpclientView.visibility = if (this.expand1) View.VISIBLE else View.GONE
                if(this.expand1) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand1 = !this.expand1
                    notifyDataSetChanged()
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList!!.size

}

