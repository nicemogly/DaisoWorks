package com.example.daisoworks

//import com.example.daisoworks.ui.itemmaster.HerpitemFragment
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.adapter.ExpandableAdapter1
import com.example.daisoworks.adapter.ExpandableAdapterDmsNotice
import com.example.daisoworks.data.CorpId
import com.example.daisoworks.data.DataItem1
import com.example.daisoworks.data.chartC
import com.example.daisoworks.databinding.FragmentTab2Binding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class fragment_tab2 : Fragment() {

    private var _binding: FragmentTab2Binding? = null
    private val binding get() = _binding!!

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService

    //공지사항
    private  var languageList = ArrayList<CorpId>()
    private lateinit var expandableAdapterDmsNotice: ExpandableAdapterDmsNotice

    private var languageList1 = ArrayList<DataItem1>()
    private lateinit var expandableAdapter1: ExpandableAdapter1

    private var comId : String = ""
    private val chartAList = mutableListOf<Float>()
    private var id1 : String = ""
    //var data: DataItemDetail1? = null
    //var dataList1: List<DataItemDetail1>? = null

    //내부저장소 변수 설정
    companion object{
        lateinit var prefs: PreferenceUtil
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTab2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        prefs = PreferenceUtil(requireContext())

       // populateGraphData()
        id1 =  prefs.getString("id", "none")
       //   id1 = "AH1506150"
        //로그인시 담아놓은 ID를 가지고  API통신시 파라미터값으로 활용함.
        comId = prefs.getString("id", "none")


        Log.d("MELONG" , comId)

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        //배열 초기화
        languageList = ArrayList<CorpId>()
        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getData(supplementService,"${BuildConfig.API_KEY}",comId)

        languageList1 = ArrayList<DataItem1>()


        binding.rvList.layoutManager = LinearLayoutManager(requireContext())

       // expandableAdapterDmsNotice.notifyDataSetChanged()
        // define layout manager for the Recycler view
        // binding.root.findViewById<RecyclerView>(R.id.rv_list).layoutManager = LinearLayoutManager(requireContext())
        // attach adapter to the recyclerview
       // binding.rvList1.layoutManager = LinearLayoutManager(requireContext())
        //expandableAdapter1 = ExpandableAdapter1(languageList1)

      //  binding.root.findViewById<RecyclerView>(R.id.rv_list1).adapter = expandableAdapter1

        var now = LocalDate.now()

        var Strnow = now.format(DateTimeFormatter.ofPattern("yyyy"))
       // Strnow = "2024"

        //getChartLoad(supplementService,"${BuildConfig.API_KEY}" , id1 , Strnow)
        getChartLoad(supplementService,"${BuildConfig.API_KEY}" , id1 , Strnow)


        return root
    }

    override fun onResume() {
        super.onResume()
        val root: View = binding.root

        prefs = PreferenceUtil(requireContext())

        // populateGraphData()
        id1 =  prefs.getString("id", "none")
        //   id1 = "AH1506150"
        //로그인시 담아놓은 ID를 가지고  API통신시 파라미터값으로 활용함.
        comId = prefs.getString("id", "none")


        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        //배열 초기화
        languageList = ArrayList<CorpId>()
        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getData(supplementService,"${BuildConfig.API_KEY}",comId)

        languageList1 = ArrayList<DataItem1>()


        binding.rvList.layoutManager = LinearLayoutManager(requireContext())

        // expandableAdapterDmsNotice.notifyDataSetChanged()
        // define layout manager for the Recycler view
        // binding.root.findViewById<RecyclerView>(R.id.rv_list).layoutManager = LinearLayoutManager(requireContext())
        // attach adapter to the recyclerview
        // binding.rvList1.layoutManager = LinearLayoutManager(requireContext())
        //expandableAdapter1 = ExpandableAdapter1(languageList1)

        //  binding.root.findViewById<RecyclerView>(R.id.rv_list1).adapter = expandableAdapter1

        var now = LocalDate.now()

        var Strnow = now.format(DateTimeFormatter.ofPattern("yyyy"))
       // Strnow = "2024"

        //getChartLoad(supplementService,"${BuildConfig.API_KEY}" , id1 , Strnow)
        getChartLoad(supplementService,"${BuildConfig.API_KEY}" , id1 , Strnow)

    }

    //Retrofit Object 설정
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
        @GET("dmsnotice")
        fun noticeView1(
            @Query("apikey") param1: String,
            @Query("mUserId") param2: String
        ): Call<List<CorpId>>

        @GET("chartdmsc")
        fun getChartC(
            @Query("apikey") param1: String,
            @Query("mUserId") param2: String,
            @Query("stdYear") param3: String
        ): Call<List<chartC>>



    }

    // API RETROFIT2 호출 함수 구현( HR에서 제공해준 API주소에 인자값 셋팅 , GET방식, 파라미터1, 2 는 HR에서 제공해준값 ,파라미터 3,4 는 앱에서 던짐.)
    // Callback시 GSON 형식에 맞게끔 데이터 클래스 담아야 함.여기서 에러주의, Json 형식 맞아야하고 GSON 변환도 맞아야함.
    private fun getData(service: RetrofitService, keyword1:String, keyword2: String){
        service.noticeView1(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<CorpId>> {
                    override  fun onFailure(call: Call<List<CorpId>>, error: Throwable) {
                        Log.d("CorpId", "문제실패원인: {$error}")
                    }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<CorpId>>,
                response: Response<List<CorpId>>
            ) {

                val responseBody = response.body()!!
                languageList.clear()
                for (comCdData1 in responseBody) {

                    languageList.add(CorpId(comCdData1.boardTitle, comCdData1.boardContents, false)) // CorpId에 필요한 값을 전달*/
                }
                expandableAdapterDmsNotice = ExpandableAdapterDmsNotice(languageList)
                binding.root.findViewById<RecyclerView>(R.id.rv_list).adapter = expandableAdapterDmsNotice

            }
        })
    }



    private fun getChartLoad(service: RetrofitService, keyword1:String, keyword2:String ,keyword3: String ){
        service.getChartC(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<chartC>> {

            override  fun onFailure(call: Call<List<chartC>>, error: Throwable) {
                Log.d("chartC", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<chartC>>,
                response: Response<List<chartC>>
            ) {
                val responseBody = response.body()!!


                var barChartView = binding.root.findViewById<BarChart>(R.id.chartConsumptionGraph1)

                val barWidth: Float
                val barSpace: Float
                val groupSpace: Float
                val groupCount = 12

                barWidth = 0.9f
                barSpace = 0.00f
                groupSpace = 0.56f

                var xAxisValues = ArrayList<String>()
                xAxisValues.add("")
                xAxisValues.add("01월")
                xAxisValues.add("02월")
                xAxisValues.add("03월")
                xAxisValues.add("04월")
                xAxisValues.add("05월")
                xAxisValues.add("06월")
                xAxisValues.add("07월")
                xAxisValues.add("08월")
                xAxisValues.add("09월")
                xAxisValues.add("10월")
                xAxisValues.add("11월")
                xAxisValues.add("12월")

                var yValueGroup1 = ArrayList<BarEntry>()
                //  var yValueGroup2 = ArrayList<BarEntry>()

                // draw the graph
                var barDataSet1: BarDataSet
                //var barDataSet2: BarDataSet

                for (dmschartClist in responseBody) {
                    yValueGroup1.add(BarEntry(1f, floatArrayOf(dmschartClist.avgLt01.toFloat())))
                    yValueGroup1.add(BarEntry(2f, floatArrayOf(dmschartClist.avgLt02.toFloat())))
                    yValueGroup1.add(BarEntry(3f, floatArrayOf(dmschartClist.avgLt03.toFloat())))
                    yValueGroup1.add(BarEntry(4f, floatArrayOf(dmschartClist.avgLt04.toFloat())))
                    yValueGroup1.add(BarEntry(5f, floatArrayOf(dmschartClist.avgLt05.toFloat())))
                    yValueGroup1.add(BarEntry(6f, floatArrayOf(dmschartClist.avgLt06.toFloat())))
                    yValueGroup1.add(BarEntry(7f, floatArrayOf(dmschartClist.avgLt07.toFloat())))
                    yValueGroup1.add(BarEntry(8f, floatArrayOf(dmschartClist.avgLt08.toFloat())))
                    yValueGroup1.add(BarEntry(9f, floatArrayOf(dmschartClist.avgLt09.toFloat())))
                    yValueGroup1.add(BarEntry(10f, floatArrayOf(dmschartClist.avgLt10.toFloat())))
                    yValueGroup1.add(BarEntry(11f, floatArrayOf(dmschartClist.avgLt11.toFloat())))
                    yValueGroup1.add(BarEntry(12f, floatArrayOf(dmschartClist.avgLt12.toFloat())))
                }



                barDataSet1 = BarDataSet(yValueGroup1, "연도별 리드타임")
               // barDataSet1.setColors(Color.BLUE, Color.RED)
                barDataSet1.setColors(Color.BLUE)
                barDataSet1.label = "리드타임"
                barDataSet1.setDrawIcons(true)
                barDataSet1.setDrawValues(true)



                //barDataSet2 = BarDataSet(yValueGroup2, "")

                //barDataSet2.label = "2017"
                //barDataSet2.setColors(Color.YELLOW, Color.RED)

                //barDataSet2.setDrawIcons(false)
                //barDataSet2.setDrawValues(false)

                //var barData = BarData(barDataSet1, barDataSet2)
                var barData = BarData(barDataSet1)

                barChartView.description.isEnabled = false
                barChartView.description.textSize = 0f
                barData.setValueFormatter(LargeValueFormatter())
                barChartView.setData(barData)
                barChartView.getBarData().setBarWidth(barWidth)
                barChartView.getXAxis().setAxisMinimum(1f)
                barChartView.getXAxis().setAxisMaximum(12f)
                //  barChartView.groupBars(0f, groupSpace, barSpace)
                //     barChartView.setFitBars(true)
                barChartView.getData().setHighlightEnabled(false)
                barChartView.invalidate()

                // set bar label
                var legend = barChartView.legend
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
                legend.setDrawInside(true)

                var legenedEntries = arrayListOf<LegendEntry>()

                legenedEntries.add(LegendEntry("LeadTime", Legend.LegendForm.SQUARE, 8f, 8f, null, Color.BLUE))
            //    legenedEntries.add(LegendEntry("2017", Legend.LegendForm.SQUARE, 8f, 8f, null, Color.BLUE))

                legend.setCustom(legenedEntries)

                legend.setYOffset(2f)
                legend.setXOffset(2f)
                legend.setYEntrySpace(0f)
                legend.setTextSize(9f)
//
                val xAxis = barChartView.getXAxis()
                xAxis.setGranularity(1f)
                xAxis.setGranularityEnabled(true)
                xAxis.setCenterAxisLabels(false)
//
                xAxis.setDrawGridLines(true)
      //  xAxis.textSize = 9f
//
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
       xAxis.setValueFormatter(IndexAxisValueFormatter(xAxisValues))
//
                xAxis.setLabelCount(12)
           xAxis.mAxisMaximum = 1f
//        xAxis.setCenterAxisLabels(true)
        //             xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 1f
    //   xAxis.spaceMax = 1f

//        barChartView.setVisibleXRangeMaximum(12f)
//        barChartView.setVisibleXRangeMinimum(12f)
     //           barChartView.setDragEnabled(true)

                //Y-axis
                barChartView.getAxisRight().setEnabled(false)
                barChartView.setScaleEnabled(true)

                val leftAxis = barChartView.getAxisLeft()
                leftAxis.setValueFormatter(LargeValueFormatter())
                leftAxis.setDrawGridLines(true)
                leftAxis.setSpaceTop(1f)
                leftAxis.setAxisMinimum(1f)


                barChartView.data = barData
                barChartView.setVisibleXRange(1f, 13f)
            }

        })
    }

//    fun populateGraphData() {
//
//        var barChartView1 = binding.root.findViewById<BarChart>(R.id.chartConsumptionGraph1)
//
//        val barWidth: Float
//        val barSpace: Float
//        val groupSpace: Float
//        val groupCount = 12
//
//        barWidth = 0.9f
//        barSpace = 0.00f
//        groupSpace = 0.56f
//
//        var xAxisValues = ArrayList<String>()
//        xAxisValues.add("01")
//        xAxisValues.add("02")
//        xAxisValues.add("03")
//        xAxisValues.add("04")
//        xAxisValues.add("05")
//        xAxisValues.add("06")
//        xAxisValues.add("07")
//        xAxisValues.add("08")
//        xAxisValues.add("09")
//        xAxisValues.add("10")
//        xAxisValues.add("11")
//        xAxisValues.add("12")
//
//        var yValueGroup1 = ArrayList<BarEntry>()
//      //  var yValueGroup2 = ArrayList<BarEntry>()
//
//        // draw the graph
//        var barDataSet1: BarDataSet
//        //var barDataSet2: BarDataSet
//
//
//        yValueGroup1.add(BarEntry(1f, floatArrayOf(9.toFloat(), 3.toFloat())))
//        //yValueGroup2.add(BarEntry(1f, floatArrayOf(2.toFloat(), 7.toFloat())))
//
//
//        yValueGroup1.add(BarEntry(2f, floatArrayOf(3.toFloat(), 3.toFloat())))
//        //yValueGroup2.add(BarEntry(2f, floatArrayOf(4.toFloat(), 15.toFloat())))
//
//        yValueGroup1.add(BarEntry(3f, floatArrayOf(3.toFloat(), 3.toFloat())))
//        //yValueGroup2.add(BarEntry(3f, floatArrayOf(4.toFloat(), 15.toFloat())))
//
//        yValueGroup1.add(BarEntry(4f, floatArrayOf(3.toFloat(), 3.toFloat())))
//        //yValueGroup2.add(BarEntry(4f, floatArrayOf(4.toFloat(), 15.toFloat())))
//
//
//        yValueGroup1.add(BarEntry(5f, floatArrayOf(9.toFloat(), 3.toFloat())))
//        //yValueGroup2.add(BarEntry(5f, floatArrayOf(10.toFloat(), 6.toFloat())))
//
//        yValueGroup1.add(BarEntry(6f, floatArrayOf(11.toFloat(), 1.toFloat())))
//        //yValueGroup2.add(BarEntry(6f, floatArrayOf(12.toFloat(), 2.toFloat())))
//
//
//        yValueGroup1.add(BarEntry(7f, floatArrayOf(11.toFloat(), 7.toFloat())))
//        //yValueGroup2.add(BarEntry(7f, floatArrayOf(12.toFloat(), 12.toFloat())))
//
//
//        yValueGroup1.add(BarEntry(8f, floatArrayOf(11.toFloat(), 9.toFloat())))
//        //yValueGroup2.add(BarEntry(8f, floatArrayOf(12.toFloat(), 8.toFloat())))
//
//
//        yValueGroup1.add(BarEntry(9f, floatArrayOf(11.toFloat(), 13.toFloat())))
//        //yValueGroup2.add(BarEntry(9f, floatArrayOf(12.toFloat(), 12.toFloat())))
//
//        yValueGroup1.add(BarEntry(10f, floatArrayOf(11.toFloat(), 2.toFloat())))
//        //yValueGroup2.add(BarEntry(10f, floatArrayOf(12.toFloat(), 7.toFloat())))
//
//        yValueGroup1.add(BarEntry(11f, floatArrayOf(11.toFloat(), 6.toFloat())))
//        //yValueGroup2.add(BarEntry(11f, floatArrayOf(12.toFloat(), 5.toFloat())))
//
//        yValueGroup1.add(BarEntry(12f, floatArrayOf(11.toFloat(), 2.toFloat())))
//        //yValueGroup2.add(BarEntry(12f, floatArrayOf(12.toFloat(), 3.toFloat())))
//
//
//        barDataSet1 = BarDataSet(yValueGroup1, "")
//        barDataSet1.setColors(Color.BLUE, Color.RED)
//        barDataSet1.label = "2016"
//        barDataSet1.setDrawIcons(false)
//        barDataSet1.setDrawValues(false)
//
//
//
//        //barDataSet2 = BarDataSet(yValueGroup2, "")
//
//        //barDataSet2.label = "2017"
//        //barDataSet2.setColors(Color.YELLOW, Color.RED)
//
//        //barDataSet2.setDrawIcons(false)
//        //barDataSet2.setDrawValues(false)
//
//        //var barData = BarData(barDataSet1, barDataSet2)
//        var barData = BarData(barDataSet1)
//
//        barChartView.description.isEnabled = false
//        barChartView.description.textSize = 0f
//        barData.setValueFormatter(LargeValueFormatter())
//        barChartView.setData(barData)
//        barChartView.getBarData().setBarWidth(barWidth)
//        barChartView.getXAxis().setAxisMinimum(1f)
//        barChartView.getXAxis().setAxisMaximum(12f)
//      //  barChartView.groupBars(0f, groupSpace, barSpace)
//      //     barChartView.setFitBars(true)
//        barChartView.getData().setHighlightEnabled(false)
//        barChartView.invalidate()
//
//        // set bar label
//        var legend = barChartView.legend
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
//        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
//        legend.setDrawInside(false)
//
//        var legenedEntries = arrayListOf<LegendEntry>()
//
//        legenedEntries.add(LegendEntry("2016", Legend.LegendForm.SQUARE, 8f, 8f, null, Color.RED))
//        legenedEntries.add(LegendEntry("2017", Legend.LegendForm.SQUARE, 8f, 8f, null, Color.BLUE))
//
//        legend.setCustom(legenedEntries)
//
//        legend.setYOffset(2f)
//        legend.setXOffset(2f)
//        legend.setYEntrySpace(0f)
//        legend.setTextSize(9f)
////
//        val xAxis = barChartView.getXAxis()
//        xAxis.setGranularity(1f)
//      xAxis.setGranularityEnabled(true)
// xAxis.setCenterAxisLabels(false)
////
//        xAxis.setDrawGridLines(true)
//////        xAxis.textSize = 9f
////
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
////        xAxis.setValueFormatter(IndexAxisValueFormatter(xAxisValues))
////
//       xAxis.setLabelCount(12)
//        xAxis.mAxisMaximum = 1f
////        xAxis.setCenterAxisLabels(true)
// //       xAxis.setAvoidFirstLastClipping(true)
////        xAxis.spaceMin = 1f
////        xAxis.spaceMax = 1f
//
////        barChartView.setVisibleXRangeMaximum(12f)
////        barChartView.setVisibleXRangeMinimum(12f)
//        barChartView.setDragEnabled(true)
//
//        //Y-axis
//        barChartView.getAxisRight().setEnabled(false)
//        barChartView.setScaleEnabled(true)
//
//        val leftAxis = barChartView.getAxisLeft()
//        leftAxis.setValueFormatter(LargeValueFormatter())
//        leftAxis.setDrawGridLines(false)
//        leftAxis.setSpaceTop(1f)
//        leftAxis.setAxisMinimum(1f)
//
//
//        barChartView.data = barData
//        barChartView.setVisibleXRange(1f, 13f)
//    }


}