package com.example.daisoworks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.adapter.AdapterHerp
import com.example.daisoworks.adapter.ExpandableAdapter2
import com.example.daisoworks.data.DataItem
import com.example.daisoworks.data.DataSujuDetail0
import com.example.daisoworks.data.chartA
import com.example.daisoworks.databinding.FragmentTab1Binding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class fragment_tab1 : Fragment() {

    private var _binding: FragmentTab1Binding? = null
    private var languageList = ArrayList<DataItem>()
    private lateinit var expandableAdapter2: ExpandableAdapter2

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : fragment_tab1.RetrofitService

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var showChart = "T"
    // RecyclerView 가 불러올 목록
    private var data1: MutableList<DataSujuDetail0> = mutableListOf()

    //private var chartAList = ArrayList<chartA>()
    private var chartAList = mutableListOf<Float>()

    companion object{
        lateinit var prefs: PreferenceUtil
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTab1Binding.inflate(inflater, container, false)
        val root: View = binding.root


        prefs = PreferenceUtil(requireContext())
        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = fragment_tab1.RetrofitClient.getInstance()
        supplementService = retrofit.create(fragment_tab1.RetrofitService::class.java)

        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
       // getCorpCdList(supplementService,comCd,"${BuildConfig.API_KEY}")

       //var comCd = "00001"
       // var vyymm = "2024"
        var now = LocalDate.now()
        var vyymm = now.format(DateTimeFormatter.ofPattern("yyyy"))


        var comCd = prefs.getString("companycode", "0")
        var userDept = prefs.getString("memdeptgbn","0")
        var excutive = prefs.getString("excutive","F")
        var deptCde = prefs.getString("memdeptcde","0")
        var mUserId = prefs.getString("id","0")
//        userDept = "19"
//        deptCde = "118"
//        excutive = "F"
        if(excutive == "F" && (userDept == "11" || userDept == "13")){
            getChartLoad1(supplementService,"${BuildConfig.API_KEY}", comCd,  vyymm , comCd , deptCde )
           // showChart = "T"
        }else {
           // Log.d("chartmain" , "{$userDept}")
            getChartLoad(supplementService,comCd,"${BuildConfig.API_KEY}" , vyymm )
         //   showChart = "F"
        }



        val textTitleView: TextView = binding.htvTitle1

//        val barChart: BarChart = binding.barChart

        val layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, // CardView width
            ViewGroup.LayoutParams.WRAP_CONTENT // CardView height
        )



        layoutParams.setMargins(16, 16, 16, 50)
        textTitleView.text = "최근 수주"

        // Log.d("DataSujuDetail0", "0")
      //  comCd = "10000"
     //   mUserId = "AH0403030"
        getLateSuju(supplementService,"${BuildConfig.API_KEY}", comCd,  mUserId )







        //배열 초기화
        languageList = ArrayList<DataItem>()




     //   binding.rvList2.layoutManager = LinearLayoutManager(requireContext())
       // expandableAdapter2 = ExpandableAdapter2(languageList)

       // binding.root.findViewById<RecyclerView>(R.id.rv_list2).adapter = expandableAdapter2


        return root
        //return inflater.inflate(R.layout.fragment_tab1, container, false)
    }


    override fun onResume() {
        super.onResume()

        val root: View = binding.root


        prefs = PreferenceUtil(requireContext())
        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = fragment_tab1.RetrofitClient.getInstance()
        supplementService = retrofit.create(fragment_tab1.RetrofitService::class.java)

        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        // getCorpCdList(supplementService,comCd,"${BuildConfig.API_KEY}")

        //var comCd = "00001"
        // var vyymm = "2024"
        var now = LocalDate.now()
        var vyymm = now.format(DateTimeFormatter.ofPattern("yyyy"))


        var comCd = prefs.getString("companycode", "0")
        var userDept = prefs.getString("memdeptgbn","0")
        var excutive = prefs.getString("excutive","F")
        var deptCde = prefs.getString("memdeptcde","0")
        var mUserId = prefs.getString("id","0")
//        userDept = "19"
//        deptCde = "118"
//        excutive = "F"
        if(excutive == "F" && (userDept == "11" || userDept == "13")){
            getChartLoad1(supplementService,"${BuildConfig.API_KEY}", comCd,  vyymm , comCd , deptCde )
            // showChart = "T"
        }else {
            // Log.d("chartmain" , "{$userDept}")
            getChartLoad(supplementService,comCd,"${BuildConfig.API_KEY}" , vyymm )
            //   showChart = "F"
        }



        val textTitleView: TextView = binding.htvTitle1

//        val barChart: BarChart = binding.barChart

        val layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, // CardView width
            ViewGroup.LayoutParams.WRAP_CONTENT // CardView height
        )



        layoutParams.setMargins(16, 16, 16, 50)
        textTitleView.text = "최근 수주"

        // Log.d("DataSujuDetail0", "0")
      //  comCd = "10000"
       // mUserId = "AH0403030"
        getLateSuju(supplementService,"${BuildConfig.API_KEY}", comCd,  mUserId )







        //배열 초기화
        languageList = ArrayList<DataItem>()




        //   binding.rvList2.layoutManager = LinearLayoutManager(requireContext())
        // expandableAdapter2 = ExpandableAdapter2(languageList)

        // binding.root.findViewById<RecyclerView>(R.id.rv_list2).adapter = expandableAdapter2


    }



    object RetrofitClient {
        private var instance: Retrofit? = null
        private val gson = GsonBuilder().setLenient().create()

        //BASEURL 끝에 / 빠지면 에러 남.
        private const val BASE_URL = "http://59.10.47.222:3000/"

        //Retrofit 객체생성
        fun getInstance(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

            //client 없으면 GSON , JSON형태의 데이터 클래스 생성시 에러가 나는것 같음.
            //Interceptor 해서 뭔가 오류 수정작업하는것 같음.
            val client: OkHttpClient =
                OkHttpClient.Builder().addInterceptor(interceptor).build()
            if (instance == null) {
                instance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return instance!!
        }
    }

    //Retrofit Service : 전송방식(GET,POST....) , Parameter 세팅가능
    interface RetrofitService {


        @GET("chartsamt1")
        fun getChart(
            @Query("corpCd") param1: String,
            @Query("apikey") param2: String,
            @Query("yymm") param3: String
        ): Call<List<chartA>>

        @GET("chartsamt1_1")
        fun getChart1(
            @Query("apikey") param1: String,
            @Query("corpCd") param2: String,
            @Query("yymm") param3: String,
            @Query("corpCd1") param4: String,
            @Query("saleCd") param5: String
        ): Call<List<chartA>>


        @GET("sujuview0")
        fun getSujuView0(
            @Query("apikey") param1: String,
            @Query("comCode") param2: String,
            @Query("mUserId") param3: String
        ): Call<List<DataSujuDetail0>>



    }

    private fun getLateSuju(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.getSujuView0(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataSujuDetail0>> {

            override  fun onFailure(call: Call<List<DataSujuDetail0>>, error: Throwable) {
                Log.d("DataItemList1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSujuDetail0>>,
                response: Response<List<DataSujuDetail0>>
            ) {

                Log.d("DataSujuDetail0", "ok")
                data1 = mutableListOf()
                val responseBody = response.body()!!
                for (sujulist in responseBody) {
                    Log.d("DataSujuDetail0", "*****")
                    data1.add(
                        DataSujuDetail0(
                            sujulist.sujumginitno,
                            sujulist.sujudate,
                            sujulist.korstrnm,
                            sujulist.korendnm,
                            sujulist.cdenme,
                            sujulist.sujuindiqty,
                            sujulist.gdsno,
                            sujulist.gdsnmekor,
                            sujulist.buycorpcd,
                            sujulist.buygdsbcd,
                            sujulist.sujuno,
                            sujulist.rn
                        )
                    )
                }



                val adapterHerp = AdapterHerp()
                adapterHerp.listData = data1
                binding.herprecyclerView.adapter = adapterHerp
                binding.herprecyclerView.layoutManager = LinearLayoutManager(context)
                var kkkk =   AdapterHerp().itemCount
                Log.d("DataSujuDetail0-row", "${kkkk}")
                Log.d("DataSujuDetail0", "3")

            }

        })
    }


    private fun getChartLoad(service: fragment_tab1.RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.getChart(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<chartA>> {

            override  fun onFailure(call: Call<List<chartA>>, error: Throwable) {
                Log.d("chartA", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<chartA>>,
                response: Response<List<chartA>>
            ) {
                chartAList = mutableListOf<Float>()

                val responseBody = response.body()!!
                var kkkk : Float
                for(ent1 in responseBody) {
                    kkkk = 0f
                    kkkk = ent1.amt
                    chartAList.add(kkkk)
                }

              //  val chartEntry = arrayListOf<Entry>()

                var kst1 = chartAList.size
                Log.d("charta1" , "{$kst1}")

                val barEntries =   mutableListOf<BarEntry>()

                for(i in 0 until chartAList.size){
                    barEntries.add(BarEntry(i.toFloat(), chartAList[i].toFloat()))

                }

              //  val lineChart: LineChart = binding.lineChart
                val barChart: BarChart = binding.barChart




                val barDataSet = BarDataSet(barEntries, "매출").apply {
                    colors = listOf(0xFF003366.toInt()) // 남색
                    valueTextColor = 0xFF003366.toInt() // 남색
                    valueTextSize = 12f
                    valueFormatter = object : ValueFormatter() {
                        private val decimalFormat = DecimalFormat("#,###.#")

                        override fun getFormattedValue(value: Float): String {
                            return if (value.toInt() == 0) {
                                "0"
                            } else {
                                decimalFormat.format(value)
                            }
                        }
                    }
                }




                val barData = BarData(barDataSet)
                barChart.data = barData
                barChart.description.isEnabled = false


                // X축 레이블 설정
                val lineXAxis = barChart.xAxis
                lineXAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(arrayOf(
                        "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
                    ))
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f // X축의 간격을 1로 설정
                    isGranularityEnabled = true
                    //labelRotationAngle = 45f // 레이블 회전 각도 설정
                    textSize = 10f // 레이블 텍스트 크기 설정도록 함)
                    labelCount = 12 // X축 레이블의 개수 강제 설정

                    setDrawGridLines(true) // X축 그리드라인 표시
                    gridColor = 0xFFCCCCCC.toInt() // 점선 색상 (연한 회색)
                    gridLineWidth = 1f // 점선의 두께
                    enableGridDashedLine(10f, 5f, 0f) // 점선 스타일 (길이, 간격, 시작점)

                    axisMinimum = -0.5f // X축 최소값 설정 (데이터 포인트가 점선 사이에 위치하도록 조정)
                    axisMaximum = 11f
                }

                // Y축 설정
                val lineYAxis = barChart.axisLeft
                lineYAxis.apply {
                    setDrawLabels(true) // 레이블 표시
                    setDrawGridLines(false) // Y축 그리드라인 숨기기
                    axisMinimum = 0f // Y축 최소값 설정 (필요에 따라 조정)
                }

                barChart.axisRight.isEnabled = false // 오른쪽 Y축 비활성화

                barChart.invalidate() // 차트 새로 고침

            }

        })
    }



    private fun getChartLoad1(service: fragment_tab1.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.getChart1(keyword1,keyword2,keyword3,keyword4, keyword5).enqueue(object: retrofit2.Callback<List<chartA>> {

            override  fun onFailure(call: Call<List<chartA>>, error: Throwable) {
                Log.d("chartA", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<chartA>>,
                response: Response<List<chartA>>
            ) {
                val responseBody = response.body()!!
                var kkkk : Float
                for(ent1 in responseBody) {
                    kkkk = 0f
                    kkkk = ent1.amt
                    chartAList.add(kkkk)
                }

                val chartEntry = arrayListOf<Entry>()

                var kst1 = chartAList.size
                Log.d("charta1" , "{$kst1}")

                val barEntries =   mutableListOf<BarEntry>()

                for(i in 0 until chartAList.size){
                    barEntries.add(BarEntry(i.toFloat(), chartAList[i].toFloat()))

                }

                //  val lineChart: LineChart = binding.lineChart
                val barChart: BarChart = binding.barChart




                val barDataSet = BarDataSet(barEntries, "매출").apply {
                    colors = listOf(0xFF003366.toInt()) // 남색
                    valueTextColor = 0xFF003366.toInt() // 남색
                    valueTextSize = 12f
                    valueFormatter = object : ValueFormatter() {
                        private val decimalFormat = DecimalFormat("#,###.#")

                        override fun getFormattedValue(value: Float): String {
                            return if (value.toInt() == 0) {
                                "0"
                            } else {
                                decimalFormat.format(value)
                            }
                        }
                    }
                }

                val barData = BarData(barDataSet)
                barChart.data = barData
                barChart.description.isEnabled = false


                // X축 레이블 설정
                val lineXAxis = barChart.xAxis
                lineXAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(arrayOf(
                        "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
                    ))
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f // X축의 간격을 1로 설정
                    isGranularityEnabled = true
                    //labelRotationAngle = 45f // 레이블 회전 각도 설정
                    textSize = 10f // 레이블 텍스트 크기 설정도록 함)
                    labelCount = 12 // X축 레이블의 개수 강제 설정

                    setDrawGridLines(true) // X축 그리드라인 표시
                    gridColor = 0xFFCCCCCC.toInt() // 점선 색상 (연한 회색)
                    gridLineWidth = 1f // 점선의 두께
                    enableGridDashedLine(10f, 5f, 0f) // 점선 스타일 (길이, 간격, 시작점)

                    axisMinimum = -0.5f // X축 최소값 설정 (데이터 포인트가 점선 사이에 위치하도록 조정)
                    axisMaximum = 11f
                }

                // Y축 설정
                val lineYAxis = barChart.axisLeft
                lineYAxis.apply {
                    setDrawLabels(true) // 레이블 표시
                    setDrawGridLines(false) // Y축 그리드라인 숨기기
                    axisMinimum = 0f // Y축 최소값 설정 (필요에 따라 조정)
                }

                barChart.axisRight.isEnabled = false // 오른쪽 Y축 비활성화

                barChart.invalidate() // 차트 새로 고침

            }

        })
    }




}


