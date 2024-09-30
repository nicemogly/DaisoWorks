package com.example.daisoworks.adapter

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.CorpId
import com.example.daisoworks.databinding.SingleItemBinding

    class ExpandableAdapterDmsNotice(private var itemList: ArrayList<CorpId>) : RecyclerView.Adapter<ExpandableAdapterDmsNotice.ViewHolder>() {

        // create an inner class with name ViewHolder
        // It takes a view argument, in which pass the generated class of single_item.xml
        // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
        inner class ViewHolder(val binding: SingleItemBinding) : RecyclerView.ViewHolder(binding.root)

        // inside the onCreateViewHolder inflate the view of SingleItemBinding
        // and return new ViewHolder object containing this layout
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = SingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            with(holder){
                with(itemList[position]){
                    // set name of the language from the list
                    binding.tvMainName.text = this.boardTitle
                    Log.d("boardTitle7777" , this.boardTitle)
                    binding.tvDescription.text = Html.fromHtml(this.boardContents, Html.FROM_HTML_MODE_COMPACT)
                    binding.expandedView.visibility = if (this.expand) View.VISIBLE else View.GONE
                    //  if(this.expand) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_down)
                    binding.cardLayout.setOnClickListener {
                        this.expand = !this.expand
                        notifyDataSetChanged()
                    }
                }
            }
        }

        override fun getItemCount(): Int = itemList.size

}

