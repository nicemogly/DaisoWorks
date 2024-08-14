package com.example.daisoworks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.daisoworks.data.DataSujuDetailItem
import com.example.daisoworks.databinding.FragmentSuju1Binding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var _binding: FragmentSuju1Binding? = null

/**
 * A simple [Fragment] subclass.
 * Use the [Suju1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Suju1Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val binding get() = _binding!!


    private var sujuList = ArrayList<DataSujuDetailItem>()
    private lateinit var expandableAdapter3: ExpandableAdapter3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuju1Binding.inflate(inflater, container, false)
        val root: View = binding.root




        Glide.with(this).load("https://cdn.daisomall.co.kr/file/PD/20240708/fDLihH42tRGSTqojDpSQ1029927_00_00fDLihH42tRGSTqojDpSQ.jpg/dims/optimize/dims/resize/150x200")
            .into(binding.root.findViewById(R.id.iv_test));

        // define layout manager for the Recycler view
        binding.rvHerpList.layoutManager = LinearLayoutManager(requireContext())
        // attach adapter to the recyclerview
        expandableAdapter3 = ExpandableAdapter3(sujuList)

        getData()

        binding.rvHerpList.adapter = expandableAdapter3



        return root

    }


    private fun getData() {
        // 서버에서 가져온 데이터라고 가정한다.
        // create new objects and add some row data
        //name,sujuno,sujudate,sujuamt,sujuipsu,sujuitemno,sujubarcode,sujutcategory,sujuitemcategory,sujuitemdesc,sujudelicondition,sujupaymentcondition,sujurun,sujumadein,expand
        val sujuListData1= DataSujuDetailItem(
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
            "인도조건:" +"CIF",
            "결제조건:" + "세금계산서",
            "운송:" +  "일반(바이어국가)",
            "원산지:" + "중국",
            true
        )

        // add items to list
        sujuList.add(sujuListData1)
        expandableAdapter3.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Suju1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Suju1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}