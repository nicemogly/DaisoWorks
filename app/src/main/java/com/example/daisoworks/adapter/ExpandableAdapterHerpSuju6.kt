package com.example.daisoworks.adapter

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataSujuDetail6
import com.example.daisoworks.databinding.HerpSuju6Binding

class ExpandableAdapterHerpSuju6(private var itemList: List<DataSujuDetail6>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpSuju6.ViewHolder>()  {


    inner class ViewHolder(val binding: HerpSuju6Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpSuju6Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){


                if(this.etc1.isNullOrEmpty()) {
                    binding.tvetc1.text =
                        Editable.Factory.getInstance().newEditable(" ")
                }else{
                    binding.tvetc1.text =  Editable.Factory.getInstance().newEditable(this.etc1)
                }
                if(this.etc2.isNullOrEmpty()) {
                    binding.tvetc2.text =
                        Editable.Factory.getInstance().newEditable(" ")
                }else{
                    binding.tvetc2.text =  Editable.Factory.getInstance().newEditable(this.etc2)
                }
                if(this.etc3.isNullOrEmpty()) {
                    binding.tvetc3.text =
                        Editable.Factory.getInstance().newEditable(" ")
                }else{
                    binding.tvetc3.text =  Editable.Factory.getInstance().newEditable(this.etc3)
                }
                  if(this.etc4.isNullOrEmpty()) {
                    binding.tvetc4.text =
                        Editable.Factory.getInstance().newEditable(" ")
                }else{
                    binding.tvetc4.text =  Editable.Factory.getInstance().newEditable(this.etc4)

                   // binding.tvetc4.text =  Editable.Factory.getInstance().newEditable("동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 동해물과백두산이 마르고 " )

                }


                binding.expandedHerpsujuView6.visibility = if (this.expand6) View.VISIBLE else View.GONE
                if(this.expand6) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand6 = !this.expand6
                    notifyDataSetChanged()
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList!!.size

}

