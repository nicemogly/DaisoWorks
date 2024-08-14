package com.example.daisoworks.adapter


import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.daisoworks.R
import com.example.daisoworks.data.ItemCount
import com.example.daisoworks.ui.itemmaster.HerpitemFragment


class ItemListViewAdapter1(val context: HerpitemFragment, private val items: MutableList<ItemCount>): BaseAdapter() {

    override fun getCount(): Int = items.size


    override fun getItem(position: Int): ItemCount = items[position]
    override fun getItemId(position: Int): Long = position.toLong()



    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {

        Log.d("testtest" , "1111" )

        var convertView = view
        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.itemdoubleunit, parent, false)

        val item: ItemCount = items[position]
        if (convertView != null) {
            convertView.findViewById<TextView>(R.id.txtItemGdsNo).text  = item.gdsno
        }
        if (convertView != null) {
            convertView.findViewById<TextView>(R.id.txtItemBuyGdsBcd).text  = item.buygdsbcd
        }

        if (convertView != null) {

                convertView.findViewById<TextView>(R.id.txtItemButton).setBackgroundColor(Color.parseColor("#FF03DAC5"))

        }

        return convertView
    }


}