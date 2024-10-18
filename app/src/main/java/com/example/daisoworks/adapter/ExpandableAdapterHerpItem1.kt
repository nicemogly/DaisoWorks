package com.example.daisoworks.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.daisoworks.R
import com.example.daisoworks.data.DataItemDetail1
import com.example.daisoworks.databinding.HerpItem1Binding

class ExpandableAdapterHerpItem1(private var itemList: List<DataItemDetail1> ,   val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpItem1.ViewHolder>()  {



    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie SingleItemBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: HerpItem1Binding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of SingleItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpItem1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    /*itemNo,barcodeNo,itemStorePrice,itemCategory,itemDesc,itemGrade,itemSalesLead,itemIpsu,itemDetail,itemPictureUrl,clientNoP,clientPreNoP,clientNameP,
,clientNoB,clientPreNoB,clientNameB,clientNoS,clientPreNoS,clientNameS,
ownerDeptName,ownerName,ownerCompany,businessOwnerDept,businessOwnerName,shipmentDept,shipmentOwnerName,sampleNewItemNo,sampleNItemNo,sampleCsNoteNo
sampleCsNoteItemNo,exhName,exhPeriod, exhDetail*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList.get(position)){
                // set name of the language from the list
                //상품정보
                binding.tvItemNo.text =    this.itemNo
                binding.tvBarcodeNo.text = this.barcodeNo
                binding.tvItemStorePrice.text = this.itemStorePrice
                binding.tvItemCategory.text = this.itemCategory
                binding.tvItemDesc.text = this.itemDesc
                binding.tvItemGrade.text = this.itemGrade
                binding.tvItemSalesLead.text = this.itemSalesLead
                binding.tvItemIpsu.text = this.itemIpsu
                binding.tvItemDetail.text = this.itemDetail.trim()

               // binding.
                //binding.root.findViewById(com.example.daisoworks.R.id.photoView1)
               // binding.findViewById(R.id.photoView1) = this.itemPictureUrl
                //binding.root.findViewById<ImageView>(R.id.photoView1) = this.itemPictureUrl
               // binding.root.findViewById<ImageView>(R.id.photoView1).setImageURI(this.itemPictureUrl.toUri())
               // val imgUrl: String = "http://herpold.asunghmp.biz/FTP"+this.itemPictureUrl
              //  val imgUrl: String = "https://cdn.daisomall.co.kr/file/PD/20240708/fDLihH42tRGSTqojDpSQ1029927_00_00fDLihH42tRGSTqojDpSQ.jpg/dims/optimize/dims/resize/150x200"



                Handler(Looper.getMainLooper()).postDelayed({

                    val imgUrl: String =
                        "http://59.10.47.222:3000/static/" + this.itemNo + ".jpg"

                    Glide.with(binding.photoView1.context)
                        .load(imgUrl)
                        .error(android.R.drawable.stat_notify_error)
                        .into(binding.photoView1)


                    // 실행 할 코드
                }, 200)



                binding.expandedHerpitemView.visibility = if (this.expand1) View.VISIBLE else View.GONE


                if(this.expand1) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand1 = !this.expand1
                    notifyDataSetChanged()
                }

            }
        }
    }

    override fun getItemCount(): Int = itemList!!.size

}

