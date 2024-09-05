package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataSujuDetail1
import com.example.daisoworks.databinding.SingleItem3Binding


class ExpandableAdapter4(private var sujuList: List<DataSujuDetail1>) : RecyclerView.Adapter<ExpandableAdapter4.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: SingleItem3Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleItem3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //name,sujuno,sujudate,sujuamt,sujuipsu,sujuitemno,sujubarcode,sujutcategory,sujuitemcategory,sujuitemdesc,sujudelicondition,sujupaymentcondition,sujurun,sujumadein,expand

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(sujuList[position]){
                // set name of the language from the list
             //   binding.tvHerpSubject.text = this.name
              //  binding.tvSujuno.text = this.sujuno
                binding.tvSujudate.text = this.sujudate
                binding.tvSujuamt.text = this.sujuamt
                binding.tvSujuipsu.text = this.sujuipsu
                binding.tvSujuitemno.text = this.sujuitemno
                binding.tvSujubarcode.text = this.sujubarcode
                binding.tvSujutcategory.text = this.sujutcategory
                binding.tvSujuitemcategory.text = this.sujuitemcategory
                /* binding.tvSujuitemdesc.text = this.sujuitemdesc
                binding.tvSujudelicondition.text = this.sujudelicondition
                binding.tvSujupaymentcondition.text = this.sujupaymentcondition
                binding.tvSujurun.text = this.sujurun
                binding.tvSujumadein.text = this.sujumadein*/
                binding.expandedHerpView.visibility = if (this.expand1) View.VISIBLE else View.GONE
                if(this.expand1) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.cardLayout2.setOnClickListener {
                    this.expand1 = !this.expand1
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int = sujuList.size

}