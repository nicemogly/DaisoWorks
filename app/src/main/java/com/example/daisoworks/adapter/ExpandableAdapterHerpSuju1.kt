package com.example.daisoworks.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daisoworks.R
import com.example.daisoworks.data.DataSujuDetail1
import com.example.daisoworks.databinding.HerpSuju1Binding
import com.example.daisoworks.ui.suju.HerpsujuFragmentDirections

class ExpandableAdapterHerpSuju1(private var itemList: List<DataSujuDetail1>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpSuju1.ViewHolder>()  {


    inner class ViewHolder(val binding: HerpSuju1Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpSuju1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){
                // set name of the language from the list


                binding.tvsujumginitno.text = this.sujumginitno
                binding.tvsujudate.text = this.sujudate
                binding.tvsujuamt.text = this.sujuamt
                binding.tvsujuipsu.text = this.sujuipsu
                binding.tvsujuper.text = this.sujuper
                binding.tvsujutcategory.text = this.sujutcategory
                binding.tvsujuitemcategory.text = this.sujuitemcategory
                binding.tvsujuitemdesc.text = this.sujuitemdesc
                binding.tvsujuitemno.text = this.sujuitemno
                binding.tvsujudelicondition.text = this.sujudelicondition
                binding.tvsujumadein.text = this.sujumadein
                binding.tvsujubarcode.text = this.sujubarcode

                val imgUrl: String = "https://cdn.daisomall.co.kr/file/PD/20240708/fDLihH42tRGSTqojDpSQ1029927_00_00fDLihH42tRGSTqojDpSQ.jpg/dims/optimize/dims/resize/150x200"

                if (imgUrl.length > 0) {

                    Glide.with(binding.photoView1.context)
                        .load(imgUrl)
                        .error(android.R.drawable.stat_notify_error)
                        .into(binding.photoView1)

                } else {
                    Glide.with(binding.photoView1.context)
                        .load(R.drawable.asung_logo)
                        .error(android.R.drawable.stat_notify_error)
                        .into(binding.photoView1)
                }


                binding.expandedHerpsujuView.visibility = if (this.expand1) View.VISIBLE else View.GONE
                if(this.expand1) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand1 = !this.expand1
                    notifyDataSetChanged()
                }


                binding.tvsujuitemno.paintFlags = Paint.UNDERLINE_TEXT_FLAG


                val comnov =  binding.tvsujuitemno.text
                val action = HerpsujuFragmentDirections.actionNavSujuToNavItemmaster2( comno = "$comnov" )


                //품번클릭시
                binding.tvsujuitemno.setOnClickListener { view ->
                    //1. findNavController().navigate(Fragment12Directions.actionFragment12ToFragment121())
                    //2. findNavController().navigate(R.id.action_fragment12_to_fragment121)


                    Navigation.findNavController(view).navigate(action)
                }




            }
        }
    }

    override fun getItemCount(): Int = itemList!!.size

}

