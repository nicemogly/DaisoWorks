package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataClientDetail1
import com.example.daisoworks.databinding.HerpClient1Binding


class ExpandableAdapterHerpClient1(private var clientList: List<DataClientDetail1>) : RecyclerView.Adapter<ExpandableAdapterHerpClient1.ViewHolder>()  {


    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: HerpClient1Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpClient1Binding.inflate(LayoutInflater.from(parent.context), parent, false)



        return ViewHolder(binding)
    }


    /*clientNoP,clientPreNoP,clientBizNoP,clientBizNameK,clientBizAddrK,clientBizCeoK,clientBizNameE,clientBizAddrE,clientBizCeoE,
       clientBizNameC,clientBizAddrC,clientBizCeoC,clientBizCountry,clientBizKind,clientBizTel,clientBizHomepage,clientBizEmail,expand1*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder){
            with(clientList[position]){
                // set name of the language from the list
                //상품정보
                binding.tvClientNoP.text = this.clientNoP
                binding.tvClientPreNoP .text = this.clientPreNoP
                binding.tvClientBizNum.text = this.clientBizNoP
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

    override fun getItemCount(): Int = clientList.size




}

