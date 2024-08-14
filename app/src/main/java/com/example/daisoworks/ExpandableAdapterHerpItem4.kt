package com.example.daisoworks

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.data.DataItemDetail4
import com.example.daisoworks.databinding.HerpItem4Binding

class ExpandableAdapterHerpItem4(private var itemList4: List<DataItemDetail4>) : RecyclerView.Adapter<ExpandableAdapterHerpItem4.ViewHolder>()  {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: HerpItem4Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpItem4Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    /*itemNo,barcodeNo,itemStorePrice,itemCategory,itemDesc,itemGrade,itemSalesLead,itemIpsu,itemDetail,itemPictureUrl,clientNoP,clientPreNoP,clientNameP,
,clientNoB,clientPreNoB,clientNameB,clientNoS,clientPreNoS,clientNameS,
ownerDeptName,ownerName,ownerCompany,businessOwnerDept,businessOwnerName,shipmentDept,shipmentOwnerName,sampleNewItemNo,sampleNItemNo,sampleCsNoteNo
sampleCsNoteItemNo,exhName,exhPeriod, exhDetail*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList4[position]){
                // set name of the language from the list
                //상품정보
                /*    binding.tvItemNo.text = this.itemNo
                    binding.tvBarcodeNo .text = this.barcodeNo
                    binding.tvItemStorePrice.text = this.itemStorePrice
                    binding.tvItemCategory.text = this.itemCategory
                    binding.tvItemDesc.text = this.itemDesc
                    binding.tvItemGrade.text = this.itemGrade
                    binding.tvItemSalesLead.text = this.itemSalesLead
                    binding.tvItemIpsu.text = this.itemIpsu
                    binding.tvItemDetail.text = this.itemDetail*/

                //거래처정보
                binding.tvSampleNewItemNo.text = this.sampleNewItemNo
                binding.tvSampleNItemNo.text = this.sampleNItemNo
                binding.tvSampleCsNoteNo.text = this.sampleCsNoteNo
                binding.tvSampleCsNoteItemNo.text = this.sampleCsNoteItemNo
                binding.tvExhName.text = this.exhName
                binding.tvExhPeriod.text = this.exhPeriod
                binding.tvExhDetail.text =  Editable.Factory.getInstance().newEditable(this.exhDetail)


                binding.expandedHerpitemView3.visibility = if (this.expand4) View.VISIBLE else View.GONE

                if(this.expand4) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_down)
                binding.viewMoreBtn.setOnClickListener {
                    this.expand4 = !this.expand4
                    notifyDataSetChanged()
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList4.size

}