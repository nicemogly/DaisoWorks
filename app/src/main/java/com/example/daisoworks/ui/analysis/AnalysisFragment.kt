package com.example.daisoworks.ui.analysis

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.Adapter
import com.example.daisoworks.data.MovieData
import com.example.daisoworks.MovieService
import com.example.daisoworks.databinding.FragmentAnalysisBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null

    // lateinit : 초기화 이후에 계속하여 값이 바뀔 수 있을 때 , var
    // by lazy : 초기화 이후에 읽기 전용 값으로 사용할 때  , val
    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!
    private val adapter by lazy { Adapter(dataList) }

    // Kotlin의 List에는 List와 MutableList가 있다.
    // List는 읽기 전용이며 MutableList는 읽기/ 쓰기가 가능.
    private val dataList = mutableListOf<MovieData.BoxOfficeResult.DailyBoxOffice?>()
    private var sdate :String = "20200101"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val analysisViewModel =
            ViewModelProvider(this).get(AnalysisViewModel::class.java)

        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //=============================API 아답터 설정 및 리사이클뷰 레이아웃 설정=======================================
        //recyclerview에 Data를 채워줄 Adapter 연결
        //현재 adapter는 상단에서 lazy로 선언하였고 Retrofit을 통해 가져올 dataList를 담고 있다.
        //RecyclerView의 LayoutManager는 RecyclerView의 아이템을 배치하고 스크롤 동작을 관리하는 데 중요한 역할을 한다.
        //LayoutManager를 사용하면 다양한 레이아웃을 쉽게 구현.
        //Android에서 주로 사용되는 LayoutManager는 세 가지가 있는데 세로(디폴트)배치,가로배치,그리드배치

        binding.rvMovie.adapter = adapter
        //activity 이면 this , fragment 이면 activity 또는 requireContext() 사용
        binding.rvMovie.layoutManager = LinearLayoutManager(requireContext())
        //========================================================================================================

        //Spinner 에 들어갈 데이터
        var data = listOf("- 년도선택 - ","2020","2021","2022","2023","2024","2025","2026","2027","2028","2029","2030")

        //데이터와 스피터를 연결 시켜줄 adapter를 만들어 준다.
        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
        var adapter = context?.let { ArrayAdapter<String>(it, R.layout.simple_list_item_1,data) }
        //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
        binding.spinner.adapter = adapter
        //데이터가 들어가 있는 spinner 에서 선택한 아이템을 가져온다.
        binding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            //Spinner를 선택하면 해당 postiondml 날짜를 가져와서 movieRequest로 넘긴다.
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //position은 선택한 아이템의 위치를 넘겨주는 인자입니다.
                sdate =  data.get(position)
                movieRequest(sdate)
            }

            //아무것도 선택하지 않았을때 이벤트 처리 가능함.
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        return root
    }

    //스피너에서 년도만 받은것을 완전한 날짜형태로 바꾸어줌.
    fun concat(s1: String, s2: String): String {
        return "$s1$s2"
    }

    //스피너에서 특정항목을 클릭했을때 날짜를 파라미터로 받음.
    fun movieRequest(sdate :String) {

        //1.Retrofit 객체 초기화
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://www.kobis.or.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //2. 서비스 객체 생성
        val apiService: MovieService = retrofit.create(MovieService::class.java)
        //3. Call 객체 생성
        val inputDate = concat(sdate, "0101")
        val movieCall = apiService.getmovieinfo(
            "3121fdf1a6b3e1ab29fa53ed59035b32", // 여기서 APikey는 실제 본인의 APi key값으로 대체합니다.
            inputDate,
            "10",
            "N",
            "K",
            ""
        )

        if (!dataList.isEmpty()) {
            dataList.clear()
        }
        movieCall.enqueue(object : Callback<MovieData> {
            override fun onResponse(call: Call<MovieData>, response: Response<MovieData>) {
                val data = response.body()

                val movieinfo = data?.boxOfficeResult?.dailyBoxOfficeList

                if (!movieinfo.isNullOrEmpty()) {
                    movieinfo?.let { info ->
                        info.forEach {
                            dataList.add(it)  //실제 API 제공받은값을 하나씩 삽입함.
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onFailure(call: Call<MovieData>, t: Throwable) {

                call.cancel()
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()


        _binding = null
    }
}