package com.example.daisoworks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataClientDetail2
import com.example.daisoworks.databinding.HerpClient2Binding


class ExpandableAdapterHerpClient2(private var itemList: List<DataClientDetail2>, val context: Context? ) : RecyclerView.Adapter<ExpandableAdapterHerpClient2.ViewHolder>()  {


    inner class ViewHolder(val binding: HerpClient2Binding) : RecyclerView.ViewHolder(binding.root){


        fun bind(item: DataClientDetail2) {


         //   binding.model = item

             binding.rvHerpClientlist21.adapter = ExpandableAdapterHerpClient22(context , item.innerList)
             binding.rvHerpClientlist21.layoutManager = LinearLayoutManager(context)
        }
    }

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpClient2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)


        with(holder){
            with(itemList[position]){



                binding.expandedHerpclientView1.visibility = if (this.expand2) View.VISIBLE else View.GONE


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

    override fun getItemCount(): Int = itemList.size

}