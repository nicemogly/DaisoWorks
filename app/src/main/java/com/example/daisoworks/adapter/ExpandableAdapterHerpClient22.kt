package com.example.daisoworks.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.DataClientDetail22
import com.example.daisoworks.databinding.HerpClient21Binding
import com.example.daisoworks.ui.client.HerpclientFragmentDirections


class ExpandableAdapterHerpClient22(val context: Context?, private var clientList2: List<DataClientDetail22>) : RecyclerView.Adapter<ExpandableAdapterHerpClient22.ViewHolder>()  {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: HerpClient21Binding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: DataClientDetail22) {
            binding.tvClientItemNo.text = item.clientItemNo
            binding.tvClientItemName.text = item.clientItemName

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpClient21Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(clientList2[position]){

                holder.bind(clientList2[position])
                binding.tvClientItemNo.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                val comnov =  binding.tvClientItemNo.text
                val action = HerpclientFragmentDirections.actionNavClientToNavItemmaster3( comno = "$comnov" )


                //화면에서 생산 거래처 번호 클릭시 네비게이션 지정된데로 이동함.
                binding.tvClientItemNo.setOnClickListener { view ->
                    //1. findNavController().navigate(Fragment12Directions.actionFragment12ToFragment121())
                    //2. findNavController().navigate(R.id.action_fragment12_to_fragment121)
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }
    }

    override fun getItemCount(): Int = clientList2.size

}