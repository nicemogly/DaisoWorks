package com.example.daisoworks.ui.itemmaster



import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintSet.GONE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.R
import com.example.daisoworks.adapter.ExpandableAdapterHerpItem1
import com.example.daisoworks.adapter.ExpandableAdapterHerpItem2
import com.example.daisoworks.adapter.ExpandableAdapterHerpItem3
import com.example.daisoworks.adapter.ExpandableAdapterHerpItem4
import com.example.daisoworks.adapter.ItemListViewAdapter1
import com.example.daisoworks.data.CorpCd
import com.example.daisoworks.data.DataItemDetail1
import com.example.daisoworks.data.DataItemDetail2
import com.example.daisoworks.data.DataItemDetail3
import com.example.daisoworks.data.DataItemDetail4
import com.example.daisoworks.data.DataTrans1
import com.example.daisoworks.data.ItemCount
import com.example.daisoworks.databinding.FragmentHerpitemBinding
import com.example.daisoworks.util.LoadingDialog
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
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


class HerpitemFragment : Fragment() {

    private var _binding: FragmentHerpitemBinding? = null
    private val binding get() = _binding!!

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService
    private lateinit var adapterItem: ItemListViewAdapter1

    //상품기본정보등 화면 Data 및 Adapter
    private var itemList1 = ArrayList<DataItemDetail1>()
    private var itemList2 = ArrayList<DataItemDetail2>()
    private var itemList3 = ArrayList<DataItemDetail3>()
    private var itemList4 = ArrayList<DataItemDetail4>()
    private lateinit var ExpandableAdapterHerpItem1: ExpandableAdapterHerpItem1
    private lateinit var ExpandableAdapterHerpItem2: ExpandableAdapterHerpItem2
    private lateinit var ExpandableAdapterHerpItem3: ExpandableAdapterHerpItem3
    private lateinit var ExpandableAdapterHerpItem4: ExpandableAdapterHerpItem4

    //spinner 값 배열리스트 초기화
    private val comCdDataArr = mutableListOf<String>("바이어 선택")
    //spinner는 id,value 형태가 아니라서 value만 들어감. 그래서 Map형태로 API통신시 미리 두개를 가져오(한개는 spinner값, 검색버튼 누를때 spinner값을 코드로 치환해주기 위한 Map임.
    private var mutableComMap = mutableMapOf<String, String>()
    //spinner의 값을 가지고 위의 배열에서 바이어시디코드를 저장함.
    private   var BuyerCd : String = ""
    private var GdsNo : String = ""
    private var comCd : String = ""
    private var BuyGdsBcd : String = ""
    private var itemcount = mutableListOf<ItemCount>()
    private var itemDialog1 = mutableListOf<String>()

    //var data: DataItemDetail1? = null
    var dataList1: List<DataItemDetail1>? = null
    var dataList2: List<DataItemDetail2>? = null
    var dataList3: List<DataItemDetail3>? = null
    var dataList4: List<DataItemDetail4>? = null
    var dataList5: List<DataTrans1>? = null

    var nullable:String ?= null



    //내부저장소 변수 설정
    companion object{
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        val herpitemViewModel =
            ViewModelProvider(this).get(HerpitemViewModel::class.java)

        _binding = FragmentHerpitemBinding.inflate(inflater, container, false)

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

        prefs = PreferenceUtil(requireContext())

        //로그인시 담아놓은 회사코드를 가지고  API통신시 파라미터값으로 활용함.
         comCd = prefs.getString("companycode","0")

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getCorpCdList(supplementService,comCd,"${BuildConfig.API_KEY}")

        //데이터와 스피너를 연결 시켜줄 adapter를 만들어 준다.
        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
        //comCdDataArr는 list 변수이며 , lateinit을 통해 초기생성후 API통신 가져온값을 가지고 채움.
        var adapter = context?.let { ArrayAdapter<String>(it, R.layout.spinnerlayout ,comCdDataArr) }
        //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
        binding.spitemschbuyer.adapter = adapter

        //with scope funciton
        with(binding){
            spitemschbuyer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                    Log.d("testtest" , "${BuyerCd}")
                    if(BuyerCd=="10005"){
                        binding.button.visibility = VISIBLE
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

        //상품정보 바인딩
        binding.rvHerpItemlist1.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem1 = ExpandableAdapterHerpItem1(itemList1, context)
        binding.rvHerpItemlist1.adapter = ExpandableAdapterHerpItem1

        //거래처정보 바인딩
        binding.rvHerpItemlist2.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem2 = ExpandableAdapterHerpItem2(itemList2,context)
        binding.rvHerpItemlist2.adapter = ExpandableAdapterHerpItem2

        //담당부서 바인딩
        binding.rvHerpItemlist3.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem3 = ExpandableAdapterHerpItem3(itemList3,context)
        binding.rvHerpItemlist3.adapter = ExpandableAdapterHerpItem3

        //샘플정보 바인딩
        binding.rvHerpItemlist4.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem4 = ExpandableAdapterHerpItem4(itemList4,context)
        binding.rvHerpItemlist4.adapter = ExpandableAdapterHerpItem4

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
                        itemcount = mutableListOf<ItemCount>()
                        //   Log.d("navArgs4" , BuyerCd)
                        getItemCount(supplementService, comCd, BuyerCd, query, "${BuildConfig.API_KEY}")
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



        val args : HerpitemFragmentArgs by navArgs()
        val vcomno = args.comno

        if(vcomno=="") {

        }else{
            Log.d("navArgs" , "${vcomno}")
            BuyerCd="10005"
            GdsNo=vcomno
            binding.svItem.setQuery(GdsNo, true)

          //  binding.svItem.isIconified = false
         //   binding.spitemschbuyer.setSelection(2)

        }



        //품번이 다수일때 선택 바코드 대상 버튼 클릭시
        var rv1 = binding.listViewItem1
        rv1.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val item = parent.getItemAtPosition(position)

            Log.d("ItevSearch" , "멀티품번 클릭")
            BuyGdsBcd =  itemcount.get(position).buygdsbcd
            binding.itemChoice.visibility = View.GONE

            if(BuyGdsBcd.isNotEmpty()) {
               // searchFlag = "1"
                itemGetData1()

            }
        }



        return root
    }
    private  fun itemGetData1() {
        Log.d("ItevSearch" , "getItemList1 호출")
        //

                Log.d("ItevSearch1" , " getItemList1 ${supplementService}")
                Log.d("ItevSearch1" , " getItemList1 ${comCd}")
                Log.d("ItevSearch1" , " getItemList1 ${BuyerCd}")
                Log.d("ItevSearch1" , " getItemList1 ${BuyGdsBcd}")
                Log.d("ItevSearch1" , " getItemList1 ${GdsNo}")

        //LoadingDialog(requireContext()).show()
        showLoadingDialog()
        getItemList1(supplementService, comCd, BuyerCd, BuyGdsBcd, GdsNo, "${BuildConfig.API_KEY}")
    }

    private fun itemGetData2() {
        Log.d("ItevSearch" , "getItemList2 호출")
        //showLoadingDialog()
        Log.d("ItevSearch1" , " getItemList2 ${supplementService}")
        Log.d("ItevSearch1" , " getItemList2 ${comCd}")
        Log.d("ItevSearch1" , " getItemList2 ${BuyerCd}")
        Log.d("ItevSearch1" , " getItemList2 ${BuyGdsBcd}")
        Log.d("ItevSearch1" , " getItemList2 ${GdsNo}")

        getItemList2(supplementService, comCd, BuyerCd, BuyGdsBcd, GdsNo, "${BuildConfig.API_KEY}")
    }

    private fun itemGetData3() {
        Log.d("ItevSearch" , "getItemList3 호출")
        //showLoadingDialog()
        getItemList3(supplementService, comCd, BuyerCd, BuyGdsBcd, GdsNo, "${BuildConfig.API_KEY}")
    }
    private fun itemGetData4() {
        Log.d("ItevSearch" , "getItemList4 호출")
        //showLoadingDialog()
        getItemList4(supplementService, comCd, BuyerCd, BuyGdsBcd, GdsNo, "${BuildConfig.API_KEY}")
    }

    private fun itemGetData5(barcode1:String) {  //바코드 품번 변환
        Log.d("ItevSearch" , "getItemList5 호출")
        //showLoadingDialog()
        getItemList5(supplementService, comCd, barcode1, "${BuildConfig.API_KEY}")
    }



    private fun showLoadingDialog() {
        val dialog = LoadingDialog(requireContext())
        CoroutineScope(Main).launch {
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

    private fun itemDisplay() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpItemlist1.visibility == GONE) {
            binding.rvHerpItemlist1.visibility = VISIBLE
        }
        binding.svItem.clearFocus();
        binding.svItem.setQuery("", false)
    }
    private fun itemDisplay1() {
        Log.d("ItevSearch" , "itemDisplay1 호출")
        if( binding.rvHerpItemlist2.visibility == GONE) {
            binding.rvHerpItemlist2.visibility = VISIBLE
        }
    }
    private fun itemDisplay2() {
        Log.d("ItevSearch3" , "itemDisplay 2호출")
        if( binding.rvHerpItemlist3.visibility == GONE) {
            Log.d("ItevSearch3" , "rvHerpItemlist3 Gone이래")
            binding.rvHerpItemlist3.visibility = VISIBLE
        }
    }

    private fun itemDisplay3() {
        Log.d("ItevSearch3" , "itemDisplay 3호출")
        if( binding.rvHerpItemlist4.visibility == GONE) {
            Log.d("ItevSearch3" , "rvHerpItemlist4 Gone이래")
            binding.rvHerpItemlist4.visibility = VISIBLE
        }
    }


    private fun noitemDisplay(){
        binding.rvHerpItemlist1.visibility = View.GONE
        binding.rvHerpItemlist2.visibility = View.GONE
        binding.rvHerpItemlist3.visibility = View.GONE
        binding.rvHerpItemlist4.visibility = View.GONE
    }


    private fun getData1( a1 : DataItemDetail1) {
        // 상품정보
        itemList1.clear()
        itemList1.add(a1)
        ExpandableAdapterHerpItem1.notifyDataSetChanged()

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

        @GET("itemcount")
        fun itemCount(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("querytxt") param3: String,
            @Query("apikey") param4: String
        ): Call<List<ItemCount>>

        @GET("itemview1")
        fun itemView1(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("GdsNo") param4: String,
            @Query("apikey") param5: String
        ): Call<List<DataItemDetail1>>

        @GET("itemview2")
        fun itemView2(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("GdsNo") param4: String,
            @Query("apikey") param5: String
        ): Call<List<DataItemDetail2>>

        @GET("itemview3")
        fun itemView3(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("GdsNo") param4: String,
            @Query("apikey") param5: String
        ): Call<List<DataItemDetail3>>

        @GET("itemview4")
        fun itemView4(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("BuyGdsBcd") param3: String,
            @Query("GdsNo") param4: String,
            @Query("apikey") param5: String
        ): Call<List<DataItemDetail4>>


        @GET("itemtrans1")
        fun itemTrans1(
            @Query("comCode") param1: String,
            @Query("BuyGdsBcd") param2: String,
            @Query("apikey") param3: String
        ): Call<List<DataTrans1>>



    }

    // API RETROFIT2 호출 함수 구현( HR에서 제공해준 API주소에 인자값 셋팅 , GET방식, 파라미터1, 2 는 HR에서 제공해준값 ,파라미터 3,4 는 앱에서 던짐.)
    // Callback시 GSON 형식에 맞게끔 데이터 클래스 담아야 함.여기서 에러주의, Json 형식 맞아야하고 GSON 변환도 맞아야함.
    private fun getCorpCdList(service: RetrofitService, keyword1:String, keyword2: String){
        service.corpCd(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<CorpCd>> {
            override  fun onFailure(call: Call<List<CorpCd>>, error: Throwable) {
                Log.d("CorpCd", "창순문제실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<CorpCd>>,
                response: Response<List<CorpCd>>
            ) {
                Log.d("testtest" , "success")
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

    fun createDialog(itemcount: MutableList<String>){

        val dataList = itemcount
        val adapter = ArrayAdapter<String>(
           // requireContext(), android.R.layout.select_dialog_item ,dataList
            requireContext(), android.R.layout.select_dialog_singlechoice ,dataList
        )
        val builder = AlertDialog.Builder(context)
        builder.setTitle("동일품번 바코드 선택")
            // builder에 어뎁터 설정
        // 두 번째 매개변수에는 사용자가 선택한 항목의 순서값이 들어온다.
        builder.setAdapter(adapter){ dialogInterface: DialogInterface, i: Int ->
           // setTextView.text = "선택한 항목 : ${dataList[i]}"
            BuyGdsBcd =  dataList[i]
            if(BuyGdsBcd.isNotEmpty()) {
                itemGetData1()
               // itemGetData2()
            }
        }
        builder.setNegativeButton("취소",null)
        builder.show()
        binding.svItem.clearFocus()

    }



    //Item 갯수 체크
    private fun getItemCount(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String){
        service.itemCount(keyword1,keyword2,keyword3,keyword4).enqueue(object: retrofit2.Callback<List<ItemCount>> {
            override  fun onFailure(call: Call<List<ItemCount>>, error: Throwable) {
                Log.d("itemcount", "실패원인: {$error}")
            }
            override fun onResponse(
                call: Call<List<ItemCount>>,
                response: Response<List<ItemCount>>
            ) {
                BuyerCd = keyword2
                Log.d("ItevSearch" , "상품검색 진입")
                val responseBody = response.body()!!
                itemcount = mutableListOf<ItemCount>()
                itemDialog1 = mutableListOf<String>()
                for(itemcnt1 in responseBody) {
                    itemcount.add( ItemCount(itemcnt1.gdsno , itemcnt1.buygdsbcd))
                    itemDialog1.add(itemcnt1.buygdsbcd)
                }

                Log.d("ItevSearchcount" , "${itemcount.size}")

               // Toast.makeText(requireContext(),"조회하시는 품번이 ${itemcnt[0]}개 검색되었습니다.", Toast.LENGTH_SHORT).show()
               if(itemcount.size > 1) { //동일품번이 존재할 경우
                   //동일품번시 바코드 목록 오픈 및 선택
                   createDialog(itemDialog1)
                   Log.d("ItevSearch" , "item Multiple")
               }else if(itemcount.size == 1) { // 품번이 1개일 경우
                   BuyGdsBcd =  itemcount[0].buygdsbcd
                   GdsNo = itemcount[0].gdsno
                   Log.d("ItevSearch" , "item search one")


                   if(BuyGdsBcd.isNotEmpty()) {
                       // searchFlag = "1"
                       itemGetData1()
                   }
               }else { //품번이 존재 하지 않을 경우

                   noitemDisplay()
                   binding.txtRstText.visibility = VISIBLE
                   binding.svItem.clearFocus();
                   binding.svItem.setQuery("", false)
                   Log.d("ItevSearch" , "item No Search")
               }

                GdsNo = binding.svItem.query.toString()
                Log.d("ItevSearch" , "상품번호 + ${GdsNo}")

            }
        })
    }

    //enqueue(object: retrofit2.Callback<DataItemList1>
    //상품 일반정보 가져오기
    private fun getItemList1(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.itemView1(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataItemDetail1>> {

            override  fun onFailure(call: Call<List<DataItemDetail1>>, error: Throwable) {
                Log.d("DataItemList1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataItemDetail1>>,
                response: Response<List<DataItemDetail1>>
            ) {

                BuyerCd = keyword2
                GdsNo = keyword4


                Log.d("ItevSearch" , " getItemList1 성공 시작")
                dataList1 = response.body()
                Log.d("ItevSearch1", dataList1.toString())


                val mAdapter = dataList1?.let { ExpandableAdapterHerpItem1(it, context) }
                binding.rvHerpItemlist1.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpItemlist1.setHasFixedSize(true)
                itemDisplay()
                itemGetData2()

            }

        })
    }

    //거래처 정보 가져오기
    private fun getItemList2(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5: String){
        service.itemView2(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataItemDetail2>> {

            override  fun onFailure(call: Call<List<DataItemDetail2>>, error: Throwable) {
                Log.d("DataItemList2", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataItemDetail2>>,
                response: Response<List<DataItemDetail2>>
            ) {

                BuyerCd = keyword2
                GdsNo = keyword4
                Log.d("ItevSearch" , " getItemList2 성공 시작")

                dataList2 = response.body()
                Log.d("ItevSearch2", dataList2.toString())


                val mAdapter = dataList2?.let { ExpandableAdapterHerpItem2(it, context) }
                binding.rvHerpItemlist2.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpItemlist2.setHasFixedSize(true)
                itemDisplay1()
                itemGetData3()

            }

        })
    }


    //상품담당자 정보 가져오기
    private fun getItemList3(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5: String){
        service.itemView3(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataItemDetail3>> {

            override  fun onFailure(call: Call<List<DataItemDetail3>>, error: Throwable) {
                Log.d("DataItemList3", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataItemDetail3>>,
                response: Response<List<DataItemDetail3>>
            ) {

                BuyerCd = keyword2
                GdsNo = keyword4
                Log.d("ItevSearch" , " getItemList3 성공 시작")

                dataList3 = response.body()
                Log.d("ItevSearch3", dataList3.toString())


                val mAdapter = dataList3?.let { ExpandableAdapterHerpItem3(it, context) }
                binding.rvHerpItemlist3.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpItemlist3.setHasFixedSize(true)
                itemDisplay2()
                itemGetData4()

            }

        })
    }


    private fun getItemList4(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5: String){
        service.itemView4(keyword1,keyword2,keyword3,keyword4, keyword5).enqueue(object: retrofit2.Callback<List<DataItemDetail4>> {

            override  fun onFailure(call: Call<List<DataItemDetail4>>, error: Throwable) {
                Log.d("DataItemList4", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataItemDetail4>>,
                response: Response<List<DataItemDetail4>>
            ) {

                BuyerCd = keyword2
                GdsNo = keyword4
                Log.d("ItevSearch" , " getItemList4 성공 시작")

                dataList4 = response.body()
                Log.d("ItevSearch3", dataList4.toString())


                val mAdapter = dataList4?.let { ExpandableAdapterHerpItem4(it, context) }
                binding.rvHerpItemlist4.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpItemlist4.setHasFixedSize(true)
                itemDisplay3()

            }

        })
    }



    private fun getItemList5(service: RetrofitService, keyword1:String, keyword2:String, keyword3: String){
        service.itemTrans1(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataTrans1>> {

            override  fun onFailure(call: Call<List<DataTrans1>>, error: Throwable) {
                Log.d("DataTrans1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataTrans1>>,
                response: Response<List<DataTrans1>>
            ) {

               // BuyerCd = keyword2
                Log.d("ItevSearch" , " getItemList4 성공 시작")

               // dataList5 = response.body()
                Log.d("itemTrans1", dataList5.toString())

/*
                val mAdapter = dataList5?.let { ExpandableAdapterHerpItem4(it, context) }
                binding.rvHerpItemlist5.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpItemlist4.setHasFixedSize(true)*/
//                itemDisplay3()

            }

        })
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

}