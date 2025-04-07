package com.example.daisoworks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R


class ItemAdapter(private val itemList: MutableList<String>, private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {


    // ViewHolder 정의
    class ItemViewHolder(itemView: View) :  RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.itemText)
        val buttonDelete: ImageView = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val barcode = itemList[position]

        holder.textView.text = itemList[position]
        // 삭제 버튼 클릭 이벤트
        holder.buttonDelete.setOnClickListener {
            onDeleteClick(barcode)
        }

    }

    override fun getItemCount(): Int = itemList.size

    fun addItem(item: String) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1) // RecyclerView 업데이트
    }

    // 바코드 삭제
    fun deleteBarcode(barcode: String) {
        val position = itemList.indexOf(barcode)
        if (position != -1) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun getAllData(): List<String> {
        return itemList
    }



    // 스캔된 값이 이미 리스트에 있는지 확인
    fun isDuplicate(value: String): Boolean {
        return itemList.contains(value)
    }


}