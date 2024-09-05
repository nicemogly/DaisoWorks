package com.example.daisoworks.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataSujuDetail4
import com.example.daisoworks.databinding.HerpSuju4Binding
import com.example.daisoworks.ui.suju.HerpsujuFragmentDirections

class ExpandableAdapterHerpSuju4(private var itemList: List<DataSujuDetail4>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpSuju4.ViewHolder>()  {

    inner class ViewHolder(val binding: HerpSuju4Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpSuju4Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){

                binding.tvclntNmKor.text = this.clnt_nm_kor
                binding.tvcdeNme.text = this.cde_nme
                binding.tvcdeNmea.text = this.cde_nmea



                binding.expandedHerpsujuView4.visibility = if (this.expand4) View.VISIBLE else View.GONE
                if(this.expand4) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand4 = !this.expand4
                    notifyDataSetChanged()
                }


                binding.tvclntNmKor.paintFlags = Paint.UNDERLINE_TEXT_FLAG


                val comnov1 =  binding.tvclntNmKor.text
                val search1 = "["
                val search2 = "]"
                val indexOf1 = comnov1.indexOf(search1)+1
                val indexOf2 = comnov1.indexOf(search2)
                val comnov = comnov1.substring(indexOf1 until indexOf2)




                val action = HerpsujuFragmentDirections.actionNavSujuToNavClient( comno = "$comnov" )


                //품번클릭시
                binding.tvclntNmKor.setOnClickListener { view ->
                    //1. findNavController().navigate(Fragment12Directions.actionFragment12ToFragment121())
                    //2. findNavController().navigate(R.id.action_fragment12_to_fragment121)


                    Navigation.findNavController(view).navigate(action)
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList!!.size

}

