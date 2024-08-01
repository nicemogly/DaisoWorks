package com.example.daisoworks.ui.itemmaster

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.daisoworks.DataSujuDetailItem
import com.example.daisoworks.ExpandableAdapter4
import com.example.daisoworks.databinding.FragmentHerpitemBinding

class HerpitemFragment : Fragment() {

    private var _binding: FragmentHerpitemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var sujuList = ArrayList<DataSujuDetailItem>()
    private lateinit var expandableAdapter4: ExpandableAdapter4

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val herpitemViewModel =
            ViewModelProvider(this).get(HerpitemViewModel::class.java)

        _binding = FragmentHerpitemBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var data = listOf("바이어선택","아성다이소","DAISO IND..")

        //데이터와 스피터를 연결 시켜줄 adapter를 만들어 준다.
        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
        var adapter = context?.let { ArrayAdapter<String>(it, R.layout.simple_spinner_item,data) }
        //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
        binding.spitemschbuyer.adapter = adapter

        binding.svItem.setQueryHint("품번 입력")
        binding.svItem.isSubmitButtonEnabled = true

      Glide.with(this).load("https://cdn.daisomall.co.kr/file/PD/20240708/fDLihH42tRGSTqojDpSQ1029927_00_00fDLihH42tRGSTqojDpSQ.jpg/dims/optimize/dims/resize/150x200")
            .into(binding.root.findViewById(com.example.daisoworks.R.id.photoView1))

        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // 검색 버튼 누를 때 호출

                if(query=="1000382"){

                    if( binding.photoView1.visibility == GONE) {
                        binding.photoView1.visibility = VISIBLE
                    }

                    if( binding.rvHerpItemlist.visibility == GONE) {
                        binding.rvHerpItemlist.visibility = VISIBLE
                    }



                    // define layout manager for the Recycler view
                    binding.rvHerpItemlist.layoutManager = LinearLayoutManager(requireContext())
                    // attach adapter to the recyclerview
                    expandableAdapter4 = ExpandableAdapter4(sujuList)

                    getData()

                    binding.rvHerpItemlist.adapter = expandableAdapter4
                    Toast.makeText(requireContext(),"품번 $query 검색", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),"품번 $query 이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }


                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                // 검색창에서 글자가 변경이 일어날 때마다 호출

                return true
            }
        })






        return root
    }

    private fun getData() {
        // 서버에서 가져온 데이터라고 가정한다.
        // create new objects and add some row data
        //name,sujuno,sujudate,sujuamt,sujuipsu,sujuitemno,sujubarcode,sujutcategory,sujuitemcategory,sujuitemdesc,sujudelicondition,sujupaymentcondition,sujurun,sujumadein,expand
        val sujuListData1 = DataSujuDetailItem(
            "기본정보",
            "NC2018030681",
            "수주일:" + "2018-03-06",
            "3000원",
            "입수:" + " 12 × 4",
            "품번:" + "1000382",
            "바코드:" + "8819910003825",
            "통합분류:" + "조리/식사>조리도구>식도",
            "상품분류:" + "조리/식사>조리도구>식도",
            "상품명:" + "BH컬러손잡이식도",
            "인도조건:" + "CIF",
            "결제조건:" + "세금계산서",
            "운송:" + "일반(바이어국가)",
            "원산지:" + "중국",
            true
        )


        // add items to list
        sujuList.add(sujuListData1)
        expandableAdapter4.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}