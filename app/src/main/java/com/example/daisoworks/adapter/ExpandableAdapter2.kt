package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.DataItem
import com.example.daisoworks.databinding.SingleItem2Binding

class ExpandableAdapter2(private var itemList: List<DataItem>) : RecyclerView.Adapter<ExpandableAdapter2.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: SingleItem2Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList[position]){
                // set name of the language from the list
                binding.tvSubject.text = this.name
                binding.tvContent.text = this.description
                binding.expandedView2.visibility = if (this.expand) View.VISIBLE else View.GONE
                //  if(this.expand) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_down)
                binding.cardLayout2.setOnClickListener {
                    this.expand = !this.expand
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

}

