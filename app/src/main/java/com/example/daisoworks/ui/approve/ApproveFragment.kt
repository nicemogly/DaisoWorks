package com.example.daisoworks.ui.approve



import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.Member
import com.example.daisoworks.MyAdapter
import com.example.daisoworks.databinding.FragmentApproveBinding

class ApproveFragment : Fragment() {

    private var _binding: FragmentApproveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // RecyclerView 가 불러올 목록
    private val data:MutableList<Member> = mutableListOf()
    var i = 4


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val approveViewModel =
            ViewModelProvider(this).get(ApproveViewModel::class.java)

        _binding = FragmentApproveBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textView2
        val layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, // CardView width
            ViewGroup.LayoutParams.WRAP_CONTENT // CardView height
        )

        layoutParams.setMargins(16,16,16,50)
        textView.text = "디자인진행 승인정보관리"

        initialize() // data 값 초기화

        val adapter = MyAdapter()
        adapter.listData = data
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)




        return root
    }


    private fun initialize(){
        with(data){
            add(Member("202112_000428" , "1042189" , "브라운투톤샐러드 접식(약20.5cm)브라운투톤샐러드 접식" , "김지효","2022-01-14"))
            add(Member("202112_000429" , "1042188" , "브라운투톤접시(약23 cm)", "김지효","2022-01-14"))
            add(Member("202112_000430" , "1042187" , "브라운투톤접시(약27 cm)", "이계원","2022-01-14"))
            add(Member("202112_000428" , "12827" , "필름인덱스마카(커터다이식,25mmx8m)", "이지은","2022-01-14"))
            add(Member("202205_001747" , "23720" , "파티풍선(해피벌스데이곰돌이)", "김지효","2022-01-14"))
            add(Member("202205_001959" , "20101" , "양방향후추그라인더", "김지효","2022-01-14"))
            add(Member("202206_000548" , "1041312" , "하트 크라프트 손잡이봉투(4매입)", "김지효","2022-01-14"))
            add(Member("202206_001074" , "1040303" , "극세사 양면 세차장갑", "김지효","2022-01-14"))
            add(Member("202206_001811" , "40662" , "수정토300G 클리어 병", "김지효","2022-01-14"))
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}