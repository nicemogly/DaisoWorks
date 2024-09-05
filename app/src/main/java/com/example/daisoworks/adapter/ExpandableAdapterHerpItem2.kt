package com.example.daisoworks.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.R
import com.example.daisoworks.data.DataItemDetail2
import com.example.daisoworks.databinding.HerpItem2Binding
import com.example.daisoworks.ui.itemmaster.HerpitemFragmentDirections

class ExpandableAdapterHerpItem2(private var itemList2: List<DataItemDetail2>, val context: Context?) : RecyclerView.Adapter<ExpandableAdapterHerpItem2.ViewHolder>()  {

    inner class ViewHolder(val binding: HerpItem2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HerpItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(itemList2.get(position)){
                //거래처정보
                binding.tvClientNoP.text = this.clientNoP
                binding.tvClientPreNoP.text = this.clientPreNoP
                binding.tvClientNameP.text = this.clientNameP
                binding.tvClientNoB.text = this.clientNoB
                binding.tvClientPreNoB.text = this.clientPreNoB
                binding.tvClientNameB.text = this.clientNameB
                binding.tvClientNoS.text = this.clientNoS
                binding.tvClientPreNoS.text = this.clientPreNoS
                binding.tvClientNameS.text = this.clientNameS

                binding.expandedHerpitemView1.visibility = if (this.expand2) View.VISIBLE else View.GONE


                if(this.expand2) binding.viewMoreBtn.setImageResource(R.drawable.ic_arrow_drop_up) else binding.viewMoreBtn.setImageResource(
                    R.drawable.ic_arrow_drop_down
                )
                binding.viewMoreBtn.setOnClickListener {
                    this.expand2 = !this.expand2
                    notifyDataSetChanged()
                }


                binding.tvClientNoP.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                binding.tvClientNoS.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                binding.tvClientNoB.paintFlags = Paint.UNDERLINE_TEXT_FLAG



                //프래그먼트 변경전 넘길 파라미터 셋팅 및 화면 전환, actionNavItemmasterToNavClient7이 mobile_navigation에서 파라미터 또는 화면연결선을
                //지정하면 자동으로 actionNavItemmasterToNavClient7dl 만들어져야 하나 gradle 빌더상 오류가 있슴. 자동생성 안되어 nav_graph2를생성했다 지움
                val comnov =  binding.tvClientNoP.text
                val action = HerpitemFragmentDirections.actionNavItemmasterToNavClient3( comno = "$comnov" )

                //화면에서 생산 거래처 번호 클릭시 네비게이션 지정된데로 이동함.
                binding.tvClientNoP.setOnClickListener { view ->
                    //1. findNavController().navigate(Fragment12Directions.actionFragment12ToFragment121())
                    //2. findNavController().navigate(R.id.action_fragment12_to_fragment121)
                    Navigation.findNavController(view).navigate(action)
                }

                //송금 거래처 번호
                binding.tvClientNoS.setOnClickListener { view ->
                    //1. findNavController().navigate(Fragment12Directions.actionFragment12ToFragment121())
                    //2. findNavController().navigate(R.id.action_fragment12_to_fragment121)
                    Navigation.findNavController(view).navigate(action)
                }

                //발주 거래처 번호
                binding.tvClientNoB.setOnClickListener { view ->
                    //1. findNavController().navigate(Fragment12Directions.actionFragment12ToFragment121())
                    //2. findNavController().navigate(R.id.action_fragment12_to_fragment121)
                    Navigation.findNavController(view).navigate(action)
                }


            }
        }
    }

    override fun getItemCount(): Int = itemList2!!.size

}

