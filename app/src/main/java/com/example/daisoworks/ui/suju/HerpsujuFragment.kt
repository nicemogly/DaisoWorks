package com.example.daisoworks.ui.suju

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.R
import com.example.daisoworks.adapter.ExpandableAdapterHerpSuju1
import com.example.daisoworks.adapter.ExpandableAdapterHerpSuju2
import com.example.daisoworks.adapter.ExpandableAdapterHerpSuju3
import com.example.daisoworks.adapter.ExpandableAdapterHerpSuju4
import com.example.daisoworks.adapter.ExpandableAdapterHerpSuju5
import com.example.daisoworks.adapter.ExpandableAdapterHerpSuju6
import com.example.daisoworks.data.CorpCd
import com.example.daisoworks.data.DataSujuDetail1
import com.example.daisoworks.data.DataSujuDetail2
import com.example.daisoworks.data.DataSujuDetail3
import com.example.daisoworks.data.DataSujuDetail4
import com.example.daisoworks.data.DataSujuDetail5
import com.example.daisoworks.data.DataSujuDetail6
import com.example.daisoworks.data.SujuCount
import com.example.daisoworks.databinding.FragmentHerpsujuBinding
import com.example.daisoworks.util.LoadingDialog
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class HerpsujuFragment : Fragment() {

    private var _binding: FragmentHerpsujuBinding? = null
    private val binding get() = _binding!!

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : HerpsujuFragment.RetrofitService
    //spinner의 값을 가지고 위의 배열에서 바이어시디코드를 저장함.
    private   var BuyerCd : String = ""
    private var GdsNo : String = ""
    private var comCd : String = ""
    private var BuyGdsBcd : String = ""
    private var SujuMgNo : String = ""
    private var sujucount = mutableListOf<SujuCount>()
    private var itemDialog1 = mutableListOf<String>()


    //수주정보등 화면 Data 및 Adapter
    private var sujuList1 = ArrayList<DataSujuDetail1>()
/*
    private var sujuList2 = ArrayList<DataSujuDetail2>()
    private var sujuList3 = ArrayList<DataSujuDetail3>()
    private var sujuList4 = ArrayList<DataSujuDetail4>()
    private var sujuList5 = ArrayList<DataSujuDetail5>()
    private var sujuList6 = ArrayList<DataSujuDetail6>()
*/


    private lateinit var ExpandableAdapterHerpSuju1: ExpandableAdapterHerpSuju1
/*    private lateinit var ExpandableAdapterHerpSuju2: ExpandableAdapterHerpSuju2
    private lateinit var ExpandableAdapterHerpSuju3: ExpandableAdapterHerpSuju3
    private lateinit var ExpandableAdapterHerpSuju4: ExpandableAdapterHerpSuju4
    private lateinit var ExpandableAdapterHerpSuju5: ExpandableAdapterHerpSuju5
    private lateinit var ExpandableAdapterHerpSuju6: ExpandableAdapterHerpSuju6*/
   // private lateinit var ExpandableAdapterHerpSuju6: ExpandableAdapterHerpSuju6

    var dataList1: List<DataSujuDetail1>? = null
    var dataList2: List<DataSujuDetail2>? = null
    var dataList3: List<DataSujuDetail3>? = null
    var dataList4: List<DataSujuDetail4>? = null
    var dataList5: List<DataSujuDetail5>? = null
    var dataList6: List<DataSujuDetail6>? = null

    //spinner 값 배열리스트 초기화
    private val comCdDataArr = mutableListOf<String>("바이어 선택")
    //spinner는 id,value 형태가 아니라서 value만 들어감. 그래서 Map형태로 API통신시 미리 두개를 가져오(한개는 spinner값, 검색버튼 누를때 spinner값을 코드로 치환해주기 위한 Map임.
    private var mutableComMap = mutableMapOf<String, String>()



    //내부저장소 변수 설정
    companion object{
        lateinit var prefs: PreferenceUtil
    }


    private val viewModel: HerpsujuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val herpsujuViewModel =
            ViewModelProvider(this).get(HerpsujuViewModel::class.java)

        _binding = FragmentHerpsujuBinding.inflate(inflater, container, false)

        val root: View = binding.root

        var btnBarcode = binding.button
        btnBarcode.setOnClickListener {
            //   val intentIntegrator = IntentIntegrator(activity)
            val intentIntegrator = IntentIntegrator.forSupportFragment(this) // use this instead
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()
        }

        HerpsujuFragment.prefs = PreferenceUtil(requireContext())

        //로그인시 담아놓은 회사코드를 가지고  API통신시 파라미터값으로 활용함.
        comCd = HerpsujuFragment.prefs.getString("companycode","0")

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = HerpsujuFragment.RetrofitClient.getInstance()
        supplementService = retrofit.create(HerpsujuFragment.RetrofitService::class.java)

        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getCorpCdList(supplementService,comCd,"${BuildConfig.API_KEY}")

        //데이터와 스피너를 연결 시켜줄 adapter를 만들어 준다.
        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
        //comCdDataArr는 list 변수이며 , lateinit을 통해 초기생성후 API통신 가져온값을 가지고 채움.
        var adapter = context?.let { ArrayAdapter<String>(it, R.layout.spinnerlayout ,comCdDataArr) }
        //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
        binding.spsujuschbuyer.adapter = adapter

        //with scope funciton
        with(binding){
            spsujuschbuyer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = comCdDataArr.get(position)
                    // binding.result.text = selected


                    // Map값중 일치하는 것으 찾아 리턴함.
                    fun <K, V> getKey(map: Map<K, V>, target: V): K? {
                        for ((key, value) in map)
                        {
                            if (target == value) {
                                return key
                            }
                        }
                        return null
                    }

                    //선택된 Corp_cd 의 코드값
                    BuyerCd = getKey(mutableComMap, selected).toString()


                    if(BuyerCd=="10005"){
                        binding.button.visibility = View.VISIBLE
                    }else{
                        binding.button.visibility = View.GONE
                    }

                    binding.svItem.clearFocus();
                    binding.svItem.setQuery("", false)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        } // with scope function end

        binding.svItem.setQueryHint("품번 입력")
        binding.svItem.isSubmitButtonEnabled = true
        binding.svItem.inputType = InputType.TYPE_CLASS_NUMBER

        //거래처바인딩
        binding.rvHerpSujulist1.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpSuju1 = ExpandableAdapterHerpSuju1(sujuList1, context)
        binding.rvHerpSujulist1.adapter = ExpandableAdapterHerpSuju1

        //검색버튼 클릭시
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.txtRstText.visibility = View.GONE
                //validation Check(BuyerCd,corpcd,itemcode
                //   Log.d("BuyerCd123" , BuyerCd)
                if(BuyerCd.equals("null")) {
                    showAlert1("바이어를 선택하세요")
                    return false
                } else if (comCd.equals("null")){
                    showAlert1("회사정보가 누락되었습니다.재로그인해주세요")
                    return false
                } else if (query.equals("null")){
                    showAlert1("품번을 입력해주세요")
                    return false
                }else {
                    //조회품번이 1개이상인지 체크함.
                    sujucount = mutableListOf<SujuCount>()
                    //   Log.d("navArgs4" , BuyerCd)
                    getSujuCount(supplementService, comCd, BuyerCd, query, "${BuildConfig.API_KEY}")
                    //검색 키보드 내림
                    binding.svItem.clearFocus()
                    //binding.button.visibility = View.GONE
                    return false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                return false
            }
        })



        //binding.root.findViewById<TextView>(R.id.tvsujuitemno).paintFlags = Paint.UNDERLINE_TEXT_FLAG

        return root

    }

    // API RETROFIT2 호출 함수 구현( HR에서 제공해준 API주소에 인자값 셋팅 , GET방식, 파라미터1, 2 는 HR에서 제공해준값 ,파라미터 3,4 는 앱에서 던짐.)
    // Callback시 GSON 형식에 맞게끔 데이터 클래스 담아야 함.여기서 에러주의, Json 형식 맞아야하고 GSON 변환도 맞아야함.
    private fun getCorpCdList(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2: String){
        service.corpCd(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<CorpCd>> {
            override  fun onFailure(call: Call<List<CorpCd>>, error: Throwable) {
                Log.d("CorpCd", "창순문제실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<CorpCd>>,
                response: Response<List<CorpCd>>
            ) {

                // RetroFit2 API CorpCd 결과값중 VALUE값 셋팅
                //  var  loginFlag: String? = response.body()?.loginList?.get(0)?.VALUE?.toString()

                val responseBody = response.body()!!
                //val myStringBuilder = StringBuilder()
                comCdDataArr.clear()
                mutableComMap.clear()
                for(comCdData1 in responseBody) {
                    comCdDataArr.add( comCdData1.comName)
                    mutableComMap.put(comCdData1.comCd , comCdData1.comName)
                }
            }
        })
    }


    private fun sujuDisplay() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist1.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist1.visibility = View.VISIBLE
        }
        binding.svItem.clearFocus();
        binding.svItem.setQuery("", false)
    }

    private fun sujuDisplay2() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist2.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist2.visibility = View.VISIBLE
        }
        binding.svItem.clearFocus();
        binding.svItem.setQuery("", false)
    }
    private fun sujuDisplay3() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist3.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist3.visibility = View.VISIBLE
        }
        binding.svItem.clearFocus();
        binding.svItem.setQuery("", false)
    }

    private fun sujuDisplay4() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist4.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist4.visibility = View.VISIBLE
        }
        binding.svItem.clearFocus();
        binding.svItem.setQuery("", false)
    }

    private fun sujuDisplay5() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist5.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist5.visibility = View.VISIBLE
        }
        binding.svItem.clearFocus();
        binding.svItem.setQuery("", false)
    }



    private fun sujuDisplay6() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist6.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist6.visibility = View.VISIBLE
        }
        binding.svItem.clearFocus();
        binding.svItem.setQuery("", false)
    }


    private fun showLoadingDialog() {
        val dialog = LoadingDialog(requireContext())
        CoroutineScope(Dispatchers.Main).launch {
            dialog.show()
            delay(3000)
            dialog.dismiss()
            // button.text = "Finished"
        }
    }

    private fun showAlert1( str1:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(str1)
        builder.show()
    }


    private fun noitemDisplay(){
        binding.rvHerpSujulist1.visibility = View.GONE
        binding.rvHerpSujulist2.visibility = View.GONE
        binding.rvHerpSujulist3.visibility = View.GONE
        binding.rvHerpSujulist4.visibility = View.GONE
        binding.rvHerpSujulist5.visibility = View.GONE
        binding.rvHerpSujulist6.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        @GET("buyersch")
        fun corpCd(
            @Query("comCode") param1: String,
            @Query("apikey") param2: String
        ): Call<List<CorpCd>>


        @GET("sujucount")
        fun sujuCount(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("querytxt") param3: String,
            @Query("apikey") param4: String
        ): Call<List<SujuCount>>

        @GET("sujuview1")
        fun sujuView1(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("sujuMgNo") param4: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("apikey") param5: String
        ): Call<List<DataSujuDetail1>>

        @GET("sujuview2")
        fun sujuView2(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("sujuMgNo") param4: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("apikey") param5: String
        ): Call<List<DataSujuDetail2>>
        @GET("sujuview3")
        fun sujuView3(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("sujuMgNo") param4: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("apikey") param5: String
        ): Call<List<DataSujuDetail3>>
        @GET("sujuview4")
        fun sujuView4(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("sujuMgNo") param4: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("apikey") param5: String
        ): Call<List<DataSujuDetail4>>

        @GET("sujuview5")
        fun sujuView5(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("sujuMgNo") param4: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("apikey") param5: String
        ): Call<List<DataSujuDetail5>>

        @GET("sujuview6")
        fun sujuView6(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("sujuMgNo") param4: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("apikey") param5: String
        ): Call<List<DataSujuDetail6>>

    }


    fun createDialog(sujucount1: MutableList<String>){

        val dataList = sujucount1
        val adapter = ArrayAdapter<String>(
            // requireContext(), android.R.layout.select_dialog_item ,dataList
            requireContext(), R.layout.select_daialog_singlechoice1 ,dataList
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle("수주조회 결과선택")
        // builder에 어뎁터 설정
        // 두 번째 매개변수에는 사용자가 선택한 항목의 순서값이 들어온다.
        builder.setAdapter(adapter){ dialogInterface: DialogInterface, i: Int ->
            // setTextView.text = "선택한 항목 : ${dataList[i]}"


            SujuMgNo = sujucount[i].suju_mg_no
            BuyGdsBcd = sujucount[i].buy_gds_bcd

            if(SujuMgNo.isNotEmpty()) {
                sujuGetData1()
            }
        }
        builder.setNegativeButton("취소",null)
        builder.show()
        binding.svItem.clearFocus()

    }



    //수주 갯수 체크
    private fun getSujuCount(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String){
        service.sujuCount(keyword1,keyword2,keyword3,keyword4).enqueue(object: retrofit2.Callback<List<SujuCount>> {
            override  fun onFailure(call: Call<List<SujuCount>>, error: Throwable) {
                Log.d("SujuCount", "실패원인: {$error}")
            }
            override fun onResponse(
                call: Call<List<SujuCount>>,
                response: Response<List<SujuCount>>
            ) {
                BuyerCd = keyword2

                val responseBody = response.body()!!
                sujucount = mutableListOf<SujuCount>()
                itemDialog1 = mutableListOf<String>()
                for(itemcnt1 in responseBody) {
                    sujucount.add( SujuCount(itemcnt1.suju_mginit_no , itemcnt1.suju_dte , itemcnt1.harb_hnmes , itemcnt1.harb_hnmee , itemcnt1.cde_nme , itemcnt1.suju_indi_qty, itemcnt1.suju_mg_no , itemcnt1.buy_gds_bcd))
                    var ktitle: String = ""
                    ktitle = "("+itemcnt1.suju_dte+")"+itemcnt1.suju_mg_no
                    itemDialog1.add(ktitle)
                }

                GdsNo = keyword3
                if(sujucount.size > 1) { //동일수주건이 존재할 경우
                    //동일수주품번시 바코드 목록 오픈 및 선택
                    createDialog(itemDialog1)

                }else if(sujucount.size == 1) { // 수주품번이 1개일 경우
                    BuyGdsBcd =  sujucount[0].buy_gds_bcd
                    SujuMgNo = sujucount[0].suju_mg_no

                    if(SujuMgNo.isNotEmpty()) {
                        // searchFlag = "1"
                        sujuGetData1()
                    }
                }else { // 수주건이 존재 하지 않을 경우

                    noitemDisplay()
                    binding.txtRstText.visibility = View.VISIBLE

                    binding.svItem.clearFocus();
                    binding.svItem.setQuery("", false)
                }

                GdsNo = binding.svItem.query.toString()

            }
        })
    }


    private  fun sujuGetData1() {

        showLoadingDialog()
        getSujuList1(supplementService, comCd, BuyerCd, SujuMgNo ,  BuyGdsBcd,  "${BuildConfig.API_KEY}")
    }


    private  fun sujuGetData2() {

       // showLoadingDialog()
        getSujuList2(supplementService, comCd, BuyerCd, SujuMgNo ,  BuyGdsBcd,  "${BuildConfig.API_KEY}")
    }
    private  fun sujuGetData3() {
        //showLoadingDialog()
        getSujuList3(supplementService, comCd, BuyerCd, SujuMgNo ,  BuyGdsBcd,  "${BuildConfig.API_KEY}")
    }
    private  fun sujuGetData4() {
       // showLoadingDialog()
        getSujuList4(supplementService, comCd, BuyerCd, SujuMgNo ,  BuyGdsBcd,  "${BuildConfig.API_KEY}")
    }

    private  fun sujuGetData5() {
       // showLoadingDialog()
        getSujuList5(supplementService, comCd, BuyerCd, SujuMgNo ,  BuyGdsBcd,  "${BuildConfig.API_KEY}")
    }

    private  fun sujuGetData6() {
        // showLoadingDialog()
        getSujuList6(supplementService, comCd, BuyerCd, SujuMgNo ,  BuyGdsBcd,  "${BuildConfig.API_KEY}")
    }



    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                //   Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("BacScan", "Scanned")

                var kscan = result.contents

                Log.d("BacScan", "${result.contents}")
                //itemGetData5(kscan)

                binding.svItem.setQuery("$kscan" , true)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // keyword1:corpcd , keyword2:buycropcd , keyword3:sujumgno , keyword4: buygdsbcd , keyword5:apikey
    private fun getSujuList1(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.sujuView1(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataSujuDetail1>> {

            override  fun onFailure(call: Call<List<DataSujuDetail1>>, error: Throwable) {
                Log.d("DataItemList1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSujuDetail1>>,
                response: Response<List<DataSujuDetail1>>
            ) {
                comCd = keyword1
                BuyerCd = keyword2
                SujuMgNo = keyword3
                BuyGdsBcd = keyword4
                dataList1 = response.body()

                val mAdapter = dataList1?.let { ExpandableAdapterHerpSuju1(it, context) }
                binding.rvHerpSujulist1.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist1.setHasFixedSize(true)
                sujuDisplay()
                sujuGetData2()

            }

        })
    }


    private fun getSujuList2(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.sujuView2(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataSujuDetail2>> {

            override  fun onFailure(call: Call<List<DataSujuDetail2>>, error: Throwable) {
                Log.d("DataItemList1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSujuDetail2>>,
                response: Response<List<DataSujuDetail2>>
            ) {
                comCd = keyword1
                BuyerCd = keyword2
                SujuMgNo = keyword3
                BuyGdsBcd = keyword4
                dataList2 = response.body()

                val mAdapter = dataList2?.let { ExpandableAdapterHerpSuju2(it, context) }
                binding.rvHerpSujulist2.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist2.setHasFixedSize(true)
                sujuDisplay2()
                sujuGetData3()

            }

        })
    }


    private fun getSujuList3(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.sujuView3(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataSujuDetail3>> {

            override  fun onFailure(call: Call<List<DataSujuDetail3>>, error: Throwable) {
                Log.d("DataItemList1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSujuDetail3>>,
                response: Response<List<DataSujuDetail3>>
            ) {
                comCd = keyword1
                BuyerCd = keyword2
                SujuMgNo = keyword3
                BuyGdsBcd = keyword4
                dataList3 = response.body()

                val mAdapter = dataList3?.let { ExpandableAdapterHerpSuju3(it, context) }
                binding.rvHerpSujulist3.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist3.setHasFixedSize(true)
                sujuDisplay3()
                sujuGetData4()

            }

        })
    }


    private fun getSujuList4(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.sujuView4(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataSujuDetail4>> {

            override  fun onFailure(call: Call<List<DataSujuDetail4>>, error: Throwable) {
                Log.d("DataItemList1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSujuDetail4>>,
                response: Response<List<DataSujuDetail4>>
            ) {
                comCd = keyword1
                BuyerCd = keyword2
                SujuMgNo = keyword3
                BuyGdsBcd = keyword4
                dataList4 = response.body()

                val mAdapter = dataList4?.let { ExpandableAdapterHerpSuju4(it, context) }
                binding.rvHerpSujulist4.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist4.setHasFixedSize(true)
                sujuDisplay4()
                sujuGetData5()

            }

        })
    }


    private fun getSujuList5(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.sujuView5(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataSujuDetail5>> {

            override  fun onFailure(call: Call<List<DataSujuDetail5>>, error: Throwable) {
                Log.d("DataItemList5", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSujuDetail5>>,
                response: Response<List<DataSujuDetail5>>
            ) {
                comCd = keyword1
                BuyerCd = keyword2
                SujuMgNo = keyword3
                BuyGdsBcd = keyword4
                dataList5 = response.body()

                val mAdapter = dataList5?.let { ExpandableAdapterHerpSuju5(it, context) }
                binding.rvHerpSujulist5.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist5.setHasFixedSize(true)
                sujuDisplay5()
                sujuGetData6()

            }

        })
    }


    private fun getSujuList6(service: HerpsujuFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.sujuView6(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataSujuDetail6>> {

            override  fun onFailure(call: Call<List<DataSujuDetail6>>, error: Throwable) {
                Log.d("DataItemList6", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSujuDetail6>>,
                response: Response<List<DataSujuDetail6>>
            ) {
                comCd = keyword1
                BuyerCd = keyword2
                SujuMgNo = keyword3
                BuyGdsBcd = keyword4
                dataList6 = response.body()

                val mAdapter = dataList6?.let { ExpandableAdapterHerpSuju6(it, context) }
                binding.rvHerpSujulist6.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist6.setHasFixedSize(true)
                sujuDisplay6()


            }

        })
    }



}