package com.example.daisoworks.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.data.DataClientDetail1
import com.example.daisoworks.data.DataClientDetail2
import com.example.daisoworks.data.DataClientDetail22
import com.example.daisoworks.ExpandableAdapterHerpClient1
import com.example.daisoworks.ExpandableAdapterHerpClient2
import com.example.daisoworks.ExpandableAdapterHerpClient22
import com.example.daisoworks.databinding.FragmentHerpclientBinding


class HerpclientFragment : Fragment() {

    private var _binding: FragmentHerpclientBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var clientList1 = ArrayList<DataClientDetail1>()
    private var clientList2 = ArrayList<DataClientDetail2>()
    private lateinit var ExpandableAdapterHerpClient1: ExpandableAdapterHerpClient1
    private lateinit var ExpandableAdapterHerpClient2: ExpandableAdapterHerpClient2
    private lateinit var ExpandableAdapterHerpClient22: ExpandableAdapterHerpClient22


    private val data21:MutableList<DataClientDetail22> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHerpclientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initialize() // data 값 초기화
        var data = listOf("선택","거래처명","거래처코드")
        //데이터와 스피터를 연결 시켜줄 adapter를 만들어 준다.
        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
        var adapter = context?.let { ArrayAdapter<String>(it, android.R.layout.simple_spinner_item,data) }
        //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
        binding.spclientschCode.adapter = adapter

        binding.svClient.setQueryHint("거래처코드/명")
        binding.svClient.isSubmitButtonEnabled = true

        //거래처정보 바인딩
        binding.rvHerpClientlist1.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpClient1 = ExpandableAdapterHerpClient1(clientList1)
        binding.rvHerpClientlist1.adapter = ExpandableAdapterHerpClient1

        //거래처상품 껍데기 바인딩
        binding.rvHerpClientlist2.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpClient2 = ExpandableAdapterHerpClient2(requireContext(), clientList2)
        binding.rvHerpClientlist2.adapter = ExpandableAdapterHerpClient2


        binding.svClient.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                // 검색 버튼 누를 때 호출
                binding.svClient.clearFocus()
                if(query=="201925"){

                    if( binding.rvHerpClientlist1.visibility == View.GONE) {
                        binding.rvHerpClientlist1.visibility = View.VISIBLE
                    }

                    if( binding.rvHerpClientlist2.visibility == View.GONE) {
                        binding.rvHerpClientlist2.visibility = View.VISIBLE
                    }



                   getData1()
                   getData2()
                    Toast.makeText(requireContext(),"거래처 $query 검색", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(),"거래처 $query 이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                return true
            }
        })


        val args : HerpclientFragmentArgs by navArgs()
        val vcomno = args.comno

        if(vcomno=="") {

        }else{

            binding.svClient.setQuery("$vcomno", true)
            binding.svClient.isIconified = false
            binding.spclientschCode.setSelection(2)

        }




        return root
    }

    private fun getData1() {
        // 서버에서 가져온 데이터라고 가정한다.
        /*clientNoP,clientPreNoP,clientBizNoP,clientBizNameK,clientBizAddrK,clientBizCeoK,clientBizNameE,clientBizAddrE,clientBizCeoE,
        clientBizNameC,clientBizAddrC,clientBizCeoC,clientBizCountry,clientBizKind,clientBizTel,clientBizHomepage,clientBizEmail,expand1*/

        val clientListData1 = DataClientDetail1(
            "201925",
            "CP2017040028",
            "66672928-000-09-16-0",
            "크리에잇 커머스 유한회사",
            "7층 CTR NO.53-55 락하트 완차이 HK",
            "알렉스뎅",
            "CREATE COMMERCE CO.,LIMITED",
            "7/F SPA CTR NO.53-55 LOCKHART RD WAN CHAI HK",
            "ALEX DENG",
            "深圳市科创商贸有限公司联系我们",
            "香港湾仔骆克道53-55号SPA中心7楼",
            "阿力而行等",
            "홍콩",
            "물품공급회사",
            "0662-3213988",
            "http://www.kbs.co.kr",
            "alex@yjholdsun.com"
        )


        clientList1.clear()
        clientList1.add(clientListData1)
        ExpandableAdapterHerpClient1.notifyDataSetChanged()
    }

    private fun initialize(){
        with(data21){
            add(DataClientDetail22( "1000231","BH스테이크 나이프"))
            add(DataClientDetail22( "1000239","BH나무손잡이국자"))
            add(DataClientDetail22( "1000241","BH나무손잡이건지개 (큰구멍)"))
            add(DataClientDetail22( "1000242","BH물결무늬야채칼"))
            add(DataClientDetail22( "1000319","BH나무손잡이스푼"))
        }
    }

    private fun getData2() {
        // 서버에서 가져온 데이터라고 가정한다.
        /*clientNoP,clientPreNoP,clientBizNoP,clientBizNameK,clientBizAddrK,clientBizCeoK,clientBizNameE,clientBizAddrE,clientBizCeoE,
        clientBizNameC,clientBizAddrC,clientBizCeoC,clientBizCountry,clientBizKind,clientBizTel,clientBizHomepage,clientBizEmail,expand1*/

        val clientListData2 = DataClientDetail2(
            false ,
            data21


            )


       // clientList2.clear()
        clientList2.add(clientListData2)
        ExpandableAdapterHerpClient2.notifyDataSetChanged()
    }

}