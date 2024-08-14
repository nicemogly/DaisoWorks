package com.example.daisoworks.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.daisoworks.FirstFragment
import com.example.daisoworks.R
import com.example.daisoworks.data.ApproveItem


class ApprListViewAdapter(val context: FirstFragment, private val items: MutableList<ApproveItem>): BaseAdapter() {
   // var listData2 = mutableListOf<ApproveItem>()

    override fun getCount(): Int = items.size
    //val apprposition: String , val apprname: String , val apprdate: String , val apprflag: String)

    override fun getItem(position: Int): ApproveItem = items[position]
    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        var convertView = view
        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.app_item, parent, false)

        val item: ApproveItem = items[position]
        if (convertView != null) {
            convertView.findViewById<TextView>(R.id.txtApprPosition).text  = item.apprposition
        }
        if (convertView != null) {
            convertView.findViewById<TextView>(R.id.txtApprName).text  = item.apprname
        }
        if (convertView != null) {
            convertView.findViewById<TextView>(R.id.txtApprDate).text  = item.apprdate
        }
        if (convertView != null) {
            if(item.apprflag=="대기"){
                convertView.findViewById<TextView>(R.id.txtApprButton).setBackgroundColor(Color.parseColor("#cccccccc"))
            }else{

                convertView.findViewById<TextView>(R.id.txtApprButton).setBackgroundColor(Color.parseColor("#FF03DAC5"))
            }
            convertView.findViewById<TextView>(R.id.txtApprButton).text  = item.apprflag
        }




        return convertView
    }


}