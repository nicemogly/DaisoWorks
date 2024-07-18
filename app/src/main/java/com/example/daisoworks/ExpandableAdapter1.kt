package com.example.daisoworks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.daisoworks.databinding.SingleItem1Binding

class ExpandableAdapter1(private var itemList1: List<DataItem1>) : RecyclerView.Adapter<ExpandableAdapter1.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: SingleItem1Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleItem1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList1[position]){
                // set name of the language from the list
                binding.tvSendName.text = this.sendName +"(" + this.sendDate + ")"
                binding.tvSubject.text = this.subject
                binding.expandedView1.visibility = if (this.expand) View.VISIBLE else View.GONE
                //  if(this.expand) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_down)
                binding.cardLayout1.setOnClickListener {
                    this.expand = !this.expand
                    notifyDataSetChanged()
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList1.size

}

