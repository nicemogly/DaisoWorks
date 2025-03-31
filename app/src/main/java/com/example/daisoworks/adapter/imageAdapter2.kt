package com.example.daisoworks.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daisoworks.R

class ImageAdapter2(val context: Context, val items: ArrayList<Uri>) :
    RecyclerView.Adapter<ImageAdapter2.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter2.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image2, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ImageAdapter2.ViewHolder, position: Int) {
        holder.bindItems(items[position])
//        val layoutParams = holder.itemView.layoutParams
//        layoutParams.width = 300
//        layoutParams.height =300
//        holder.itemView.layoutParams = layoutParams
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Uri) {
            val imageArea = itemView.findViewById<ImageView>(R.id.imageArea)

            Glide.with(context).load(item).into(imageArea)
        }
    }
}