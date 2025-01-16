package com.example.daisoworks

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.daisoworks.data.apirstData
import com.example.daisoworks.databinding.ActivitySujuBinding
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

class SujuActivity : AppCompatActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySujuBinding

    private var SujuNo: String = ""
    private var ItemNo: String = ""
    private var SujuBuyer: String = ""
    private var buygdsbcd: String = ""


    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService
    //spinner의 값을 가지고 위의 배열에서 바이어시디코드를 저장함.
    private   var BuyerCd : String = ""
    private var GdsNo : String = ""
    private var gdsname : String = ""
    private var sujumginitno : String = ""
    private var comCd : String = ""
    private var BuyGdsBcd : String = ""
    private var SujuMgNo : String = ""
    private var sujucount = mutableListOf<SujuCount>()
    private var itemDialog1 = mutableListOf<String>()


    //수주정보등 화면 Data 및 Adapter
    private var sujuList1 = ArrayList<DataSujuDetail1>()



    private lateinit var ExpandableAdapterHerpSuju1: ExpandableAdapterHerpSuju1


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySujuBinding.inflate(layoutInflater)
        setContentView(binding.root)

      // setSupportActionBar(binding.toolbar)

        var prefs = PreferenceUtil(this)


        binding.bticon.setOnClickListener {
            finish()
        }


        //기본 Actionbar 제목 변경
        getSupportActionBar()?.setTitle("$SujuNo($ItemNo)");
        sharedViewModel.updateText("$SujuNo($ItemNo)")
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)


        //로그인시 담아놓은 회사코드를 가지고  API통신시 파라미터값으로 활용함.
        comCd = prefs.getString("companycode","0")

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)


        //거래처바인딩
        binding.rvHerpSujulist1.layoutManager = LinearLayoutManager(this)
        ExpandableAdapterHerpSuju1 = ExpandableAdapterHerpSuju1(sujuList1, this)
        binding.rvHerpSujulist1.adapter = ExpandableAdapterHerpSuju1

        var comCd1 = prefs.getString("companycode","0")



        if (intent.hasExtra("sujuNo")) {

            SujuMgNo = intent.getStringExtra("sujuNo").toString()
            SujuMgNo = intent.getStringExtra("sujuNo").toString()
            ItemNo = intent.getStringExtra("itemNo").toString()
            BuyerCd = intent.getStringExtra("sujuBuyer").toString()
            BuyGdsBcd = intent.getStringExtra("buygdsbcd").toString()
            comCd = prefs.getString("companycode","0")
            gdsname = prefs.getString("gdsname","0")
            sujumginitno = intent.getStringExtra("sujumginitno").toString()
            binding.txtsujuname.text = sujumginitno
            sujuGetData1()
        }

    }


    //Actionbar 메뉴 클릭 이벤트
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { //뒤로 가기 버튼(활성화 후에 추가하기)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun sujuDisplay() {


        if( binding.rvHerpSujulist1.visibility == ConstraintSet.GONE) {

            binding.rvHerpSujulist1.visibility = View.VISIBLE
        }
//        binding.svItem.clearFocus();
//        binding.svItem.setQuery("", false)
    }

    private fun sujuDisplay2() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist2.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist2.visibility = View.VISIBLE
        }
    }
    private fun sujuDisplay3() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpSujulist3.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist3.visibility = View.VISIBLE
        }
    }

    private fun sujuDisplay4() {

        if( binding.rvHerpSujulist4.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist4.visibility = View.VISIBLE
        }
    }

    private fun sujuDisplay5() {

        if( binding.rvHerpSujulist5.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist5.visibility = View.VISIBLE
        }
    }



    private fun sujuDisplay6() {

        if( binding.rvHerpSujulist6.visibility == ConstraintSet.GONE) {
            binding.rvHerpSujulist6.visibility = View.VISIBLE
        }
    }


    private fun showLoadingDialog() {
        val dialog = LoadingDialog(this)
        CoroutineScope(Dispatchers.Main).launch {
            dialog.show()
            delay(1000)
            dialog.dismiss()
            // button.text = "Finished"
        }
    }

    private fun showAlert1( str1:String){
        val builder = AlertDialog.Builder(this)
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
        @GET("imgdownload")
        fun imgView1(
            @Query("apikey") param1: String,
            @Query("reqno") param2: String,
            @Query("imgUrl") param3: String?
        ): Call<List<apirstData>>

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

              //  binding.svItem.setQuery("$kscan" , true)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // keyword1:corpcd , keyword2:buycropcd , keyword3:sujumgno , keyword4: buygdsbcd , keyword5:apikey
    private fun getSujuList1(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
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


                var vtlpath1 = dataList1?.get(0)?.vtlpath
                var vgdsno = dataList1?.get(0)?.sujuitemno
                var vbcd = BuyGdsBcd

                var prefixattr3  = ""
                var attr5 = ""
                var imgUrl1 = ""


                    prefixattr3 = "http://herp.asunghmp.biz/FTP"



                attr5 = vgdsno+".jpg"
                imgUrl1 = prefixattr3+vtlpath1+"/"+BuyGdsBcd+".jpg"
                imgUrl1 = imgUrl1.trim()
                requestGet1(supplementService,"${BuildConfig.API_KEY}", attr5, imgUrl1)


                val mAdapter = dataList1?.let { ExpandableAdapterHerpSuju1(it, this@SujuActivity) }
                binding.rvHerpSujulist1.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist1.setHasFixedSize(true)
                var kkkkkkk = mAdapter?.itemCount

                Log.d("param5" , "${kkkkkkk}")
                sujuDisplay()
                sujuGetData2()

            }

        })
    }


    private fun requestGet1(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.imgView1(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<apirstData>> {


            override  fun onFailure(call: Call<List<apirstData>>, error: Throwable) {
                Log.d("apirstData", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<apirstData>>,
                response: Response<List<apirstData>>
            ) {

                Log.d("apirstData", "ok")
            }

        })
    }


    private fun getSujuList2(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
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

                val mAdapter = dataList2?.let { ExpandableAdapterHerpSuju2(it, this@SujuActivity) }
                binding.rvHerpSujulist2.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist2.setHasFixedSize(true)
                sujuDisplay2()
                sujuGetData3()

            }

        })
    }


    private fun getSujuList3(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
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

                val mAdapter = dataList3?.let { ExpandableAdapterHerpSuju3(it, this@SujuActivity) }
                binding.rvHerpSujulist3.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist3.setHasFixedSize(true)
                sujuDisplay3()
                sujuGetData4()

            }

        })
    }


    private fun getSujuList4(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
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

                val mAdapter = dataList4?.let { ExpandableAdapterHerpSuju4(it,this@SujuActivity) }
                binding.rvHerpSujulist4.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist4.setHasFixedSize(true)
                sujuDisplay4()
                sujuGetData5()

            }

        })
    }


    private fun getSujuList5(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
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

                val mAdapter = dataList5?.let { ExpandableAdapterHerpSuju5(it, this@SujuActivity) }
                binding.rvHerpSujulist5.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist5.setHasFixedSize(true)
                sujuDisplay5()
                sujuGetData6()

            }

        })
    }


    private fun getSujuList6(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
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

                val mAdapter = dataList6?.let { ExpandableAdapterHerpSuju6(it, this@SujuActivity) }
                binding.rvHerpSujulist6.adapter = mAdapter
                mAdapter?.notifyDataSetChanged()
                binding.rvHerpSujulist6.setHasFixedSize(true)
                sujuDisplay6()


            }

        })
    }


}