package com.example.daisoworks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.daisoworks.adapter.ApprListViewAdapter
import com.example.daisoworks.adapter.CommentAdapterAdapter
import com.example.daisoworks.data.DataDmsDetail1
import com.example.daisoworks.data.DataDmsDetail3
import com.example.daisoworks.data.DataDmsDetail4
import com.example.daisoworks.data.DataDmsDetail5
import com.example.daisoworks.data.apirstData
import com.example.daisoworks.databinding.ActivitySubBinding
import com.example.daisoworks.util.LoadingDialog
import com.google.gson.GsonBuilder
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
import java.text.Normalizer
import java.util.regex.Matcher
import java.util.regex.Pattern


class SubActivity : AppCompatActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()
    //private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySubBinding


    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : SubActivity.RetrofitService

    private var data9 : MutableList<String>   = mutableListOf()

    private var id1 : String = ""
    private var apprSeq : String = ""
    private var ReqNo: String = ""
    private var RevNo: String = ""
    private var reqItemDesc: String = ""
    private var apprTyp: String = ""
    private var kurl1: String  = ""
    private var kurl2: String  = ""
    private var kurl3: String  = ""

   // private val data1:MutableList<Comment> = mutableListOf()




    // RecyclerView 가 불러올 목록
    private var data:MutableList<DataDmsDetail3> = mutableListOf()
    private var data1:MutableList<DataDmsDetail3> = mutableListOf()



    private  var Ttitle : String = ""
    companion object{
        lateinit var prefs: PreferenceUtil
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferenceUtil(this)


        // RecyclerView 가 불러올 목록
        if (intent.hasExtra("reqNo")) {
            ReqNo = intent.getStringExtra("reqNo").toString()
            RevNo = intent.getStringExtra("revNo").toString()
           // reqItemDesc = intent.getStringExtra("reqItemDesc").toString()

        }


       // binding.txtReq1D.text = ReqNo


        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = SubActivity.RetrofitClient.getInstance()
        supplementService = retrofit.create(SubActivity.RetrofitService::class.java)

        getDmsDetail(supplementService,"${BuildConfig.API_KEY}",ReqNo, RevNo)






        binding.bticon.setOnClickListener {


            finish()
        }


        //initialize() // data 값 초기화
        //apprposition: 결재직잭  , apprid: 결재자 아이디 , apprname: 결재자 , apprdate: 결재일시 , apprflag:결재상태 , apprthis 결제현재여부


        //이미지 확대축소 크기 설정
        val scale = 1f
        if(scale >= binding.photoView.minimumScale && scale <= binding.photoView.maximumScale){
            binding.photoView.setScale(scale, true)
        }

        //확대축소 리스너 가능
        binding.photoView.setOnScaleChangeListener { scaleFactor , focusX , focusY ->
            if(scaleFactor > 1f) {
                //이미지가 확대되었을때 처리
            }else if (scaleFactor < 1f) {
                //이미지가 축소되었을때 처리
            }
        }

        //이미지 탭 리스너:이미지 탭했을때 위치를 리스너로 등록해서 처리할 수 있슴.
        binding.photoView.setOnScaleChangeListener { view, x, y ->
            //이미지를 탭했을때 처리
        }






    }


//    private fun initialize(){
//        //val reqNo: String , val reqSendName: String  , val reqRedvName: String  , val reqDate: String  , val reqConts: String
//        with(data1){
//            add(Comment("1042189" , "㈜아성 패션/애완/완구/목재 디자인팀 김지효(AS1610130)" , "㈜아성 주방/식사/패션/케미컬 박상연(AS1706170)" , "2022-02-04 18:17:12", "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요  안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요  안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요  안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안  녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요"))
//            add(Comment("1042189" , "㈜아성 패션/애완/완구/목재 디자인팀 김지효(AS1610130)" , "㈜아성 주방/식사/패션/케미컬 박상연(AS1706170)" , "2022-02-04 18:17:12", "안녕하세요"))
//            add(Comment("1042189" , "㈜아성 패션/애완/완구/목재 디자인팀 김지효(AS1610130)" , "㈜아성 주방/식사/패션/케미컬 박상연(AS1706170)" , "2022-02-04 18:17:12", "안녕하세요"))
//
//        }
//    }


    private fun createBottomSheet(position1:Int,pitems3: MutableList<DataDmsDetail5>){


       // Log.d("botommsheet" , "${position1}")
        try {
            var bottomSheetListView: BottomSheetListView =
                BottomSheetListView(this, position1 , pitems3)
            bottomSheetListView.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)


            bottomSheetListView.show(supportFragmentManager,  "SET_BOTTOM_SHEET")


            bottomSheetListView.setOnClickListener(object :
                BottomSheetListView.onDialogClickListener {


                override fun onClicked(clickItem: String) {
                        Log.d("bottomSheet111" , "click")
                }
            }
            )

        }catch (e:Exception){
            Log.d("testestest", "error")
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


    interface RetrofitService {
        @GET("dmsview2")
        fun getdmsDetail(
            @Query("apikey") param1: String,
            @Query("reqId") param2: String,
            @Query("revNo") param3: String
        ): Call<List<DataDmsDetail1>>

        @GET("dmsview3")
        fun getdmsDetail3(
            @Query("apikey") param1: String,
            @Query("reqId") param2: String,
            @Query("revNo") param3: String
        ): Call<List<DataDmsDetail3>>

        @GET("dmsview4")
        fun getdmsDetail4(
            @Query("apikey") param1: String,
            @Query("reqId") param2: String,
            @Query("revNo") param3: String
        ): Call<List<DataDmsDetail4>>

        @GET("dmsview5")
        fun getdmsDetail5(
            @Query("apikey") param1: String,
            @Query("reqId") param2: String,
            @Query("revNo") param3: String,
            @Query("apprSeq") param4: String,
            @Query("mUserId") param5: String
        ): Call<List<DataDmsDetail5>>





        @GET("imgdownload")
        fun imgView1(
            @Query("apikey") param1: String,
            @Query("reqno") param2: String,
            @Query("imgUrl") param3: String?
        ): Call<List<apirstData>>



    }


    private fun getDmsDetail(service: SubActivity.RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.getdmsDetail(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataDmsDetail1>> {

            override fun onFailure(call: Call<List<DataDmsDetail1>>, error: Throwable) {
                Log.d("DataDmsDetail1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataDmsDetail1>>,
                response: Response<List<DataDmsDetail1>>
            ) {
                Log.d("DMS DataDmsDetail1", "1")

                val responseBody = response.body()!!

               // data = mutableListOf()
              //  data1 = mutableListOf()

                for (dmsdetail in responseBody) {
                     binding.txtReq1D.text =  dmsdetail.reqId
                     binding.txtReq2D.text = dmsdetail.productCd
                     binding.txtReq3D.text = dmsdetail.korProductDesc
                     binding.txtReq4D.text = dmsdetail.reqDate
                     binding.txtReq5D.text = dmsdetail.dsnTypNm
                     binding.txtReq6D.text = dmsdetail.buyCompNm
                     binding.txtReq7D.text = dmsdetail.reqTypNm
                     binding.txtReq8D.text = dmsdetail.reqEmpNm
                     binding.txtReq10D.text = dmsdetail.cmplExptDate
                     binding.txtReq12D.text = dmsdetail.phEmpNm
                  //  binding.txtReq12D.text = dmsdetail.apprSeq
                    reqItemDesc = dmsdetail.korProductDesc
                    apprSeq = dmsdetail.apprSeq
                    RevNo = dmsdetail.revNo

                    var prefixattr3  = ""
                    var attr5 = ""
                    var imgUrl1 = ""

                    if(dmsdetail.productImageType == "PATH"){
                        prefixattr3 = "https://dms.asungcorp.com"
                    }else if(dmsdetail.productImageType == "BLOB") {
                      prefixattr3 = "http://herp.asunghmp.biz/FTP"
                    }

                    binding.txtAppbar.text = reqItemDesc


                    attr5 = dmsdetail.productCd+".jpg"
                    imgUrl1 = prefixattr3+dmsdetail.productImage
                    imgUrl1 = imgUrl1.trim()
                    requestGet1(supplementService,"${BuildConfig.API_KEY}", attr5, imgUrl1)


                    Handler(Looper.getMainLooper()).postDelayed({

                        val imgUrl: String =
                            "http://59.10.47.222:3000/static/" + dmsdetail.productCd + ".jpg"

                        Glide.with(binding.imageView2.context)
                            .load(imgUrl)
                            .error(android.R.drawable.stat_notify_error)
                            .into(binding.imageView2)


                        // 실행 할 코드
                    }, 500)


                }

                getDmsDetail3(supplementService,"${BuildConfig.API_KEY}",ReqNo, RevNo)  //comment



            }

        })
    }

    fun reload() {

      //  getDmsDetail5(supplementService,"${BuildConfig.API_KEY}",ReqNo, RevNo ,apprSeq, id1)  //Design Image File

        Handler(Looper.getMainLooper()).postDelayed({


            finish()
            startActivity(getIntent())



            // 실행 할 코드
        }, 2000)




     //   onResume()
     //   finish()
        //startActivity(intent);

    }


    private fun getDmsDetail3(service: SubActivity.RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.getdmsDetail3(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataDmsDetail3>> {

            override fun onFailure(call: Call<List<DataDmsDetail3>>, error: Throwable) {
                Log.d("DataDmsDetail3", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataDmsDetail3>>,
                response: Response<List<DataDmsDetail3>>
            ) {
                Log.d("DMS DataDmsDetail3", "1")

                val responseBody = response.body()!!
                Log.d("DMS DataDmsDetail3", "{$responseBody}")
                 data1 = mutableListOf()

                for (dmsdetail3 in responseBody) {
                   Log.d("DataMSDetail3" , "2")
                    data1.add(
                        DataDmsDetail3(
                            dmsdetail3.fromEmpNm,
                            dmsdetail3.toEmpNm,
                            dmsdetail3.registDate,
                            removeTag(dmsdetail3.cmnt)
                        )
                    )
                }

                val adapter1 = CommentAdapterAdapter()
                adapter1.listData1 = data1
                binding.recyclerView1.adapter = adapter1
                binding.recyclerView1.layoutManager = LinearLayoutManager(this@SubActivity)


                getDmsDetail4(supplementService,"${BuildConfig.API_KEY}",ReqNo, RevNo)  //Design Image File




            }



        })

    }

    private fun getDmsDetail4(service: SubActivity.RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.getdmsDetail4(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataDmsDetail4>> {

            override fun onFailure(call: Call<List<DataDmsDetail4>>, error: Throwable) {
                Log.d("DataDmsDetail1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataDmsDetail4>>,
                response: Response<List<DataDmsDetail4>>
            ) {
                Log.d("DMS DataDmsDetail4", "1")

                val responseBody = response.body()!!
                var kstr2 = responseBody.size

                //val myWebView= binding.webview
              var kflagint: Int = 0 ;
                for (dmsdetail4 in responseBody) {
                  //  binding.txtReq1D.text =  DataDmsDetail4.reqId

                    var prefixattr3  = ""
                    var attr5 = ""
                    var imgUrl1 = ""



                    prefixattr3 = "https://dms.asungcorp.com"

                    attr5 = dmsdetail4.origFileNm+"."+dmsdetail4.origFileExt
                    imgUrl1 = prefixattr3+dmsdetail4.srvrPath+attr5
                    imgUrl1 = imgUrl1.trim()
                    Log.d("designimage" , "{$imgUrl1}")

                    data9.add(attr5)

                    Log.d("data9", "${attr5}")

                    requestGet1(supplementService,"${BuildConfig.API_KEY}", attr5, imgUrl1)


                    Handler(Looper.getMainLooper()).postDelayed({

                        val imgUrl: String =
                            "http://59.10.47.222:3000/static/" + attr5

                        Glide.with(binding.photoView.context)
                            .load(imgUrl)
                            .error(android.R.drawable.stat_notify_error)
                            .into(binding.photoView)

                        // 실행 할 코드
                    }, 500)
                    Log.d("data9-kflag", "${kflagint}")

                    Log.d("data9-kflag", "${attr5}")
                    if(kflagint < 1) {
                         kurl3 = "http://59.10.47.222:3000/static/" + attr5


                    }
                    kflagint = kflagint + 1

                }

                kstr2 = data9.size
                Log.d("designimage" , "{$kstr2}")

                if(kstr2 > 1) {
                    binding.dssp.visibility = View.VISIBLE
                }


                // data9 = mutableListOf("1000382.jpg","1031154.jpg","1031155.jpg")
                //데이터와 스피터를 연결 시켜줄 adapter를 만들어 준다.
                //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
                var adapter9 = ArrayAdapter<String>(this@SubActivity, android.R.layout.simple_spinner_item,data9)
                //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
                binding.dssp.adapter = adapter9

                binding.dssp.selected {

                    Handler(Looper.getMainLooper()).postDelayed({

                        val imgUrl: String =
                            "http://59.10.47.222:3000/static/" + data9[it]

                        kurl3 = imgUrl
                        kurl2 =  data9[it]
                        Glide.with(binding.photoView.context)
                            .load(imgUrl)
                            .error(android.R.drawable.stat_notify_error)
                            .into(binding.photoView)


                        // 실행 할 코드
                    }, 500)


                }

                binding.btnexpan1.setOnClickListener {

                    Log.d("btnexpan1", "$kurl3")

                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse("$kurl3"))
                    startActivity(intent)
                    //myWebView.loadUrl("${kurl1}")
                }




                id1 =  SubActivity.prefs.getString("id", "none")
                //id1 = "AH1506150"
                // id1 = "AH0403070"
                // apprSeq = "1"

                getDmsDetail5(supplementService,"${BuildConfig.API_KEY}",ReqNo, RevNo ,apprSeq, id1)  //Design Image File




            }

        })
    }

    private fun getDmsDetail5(service: SubActivity.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String){
        service.getdmsDetail5(keyword1,keyword2,keyword3,keyword4,keyword5).enqueue(object: retrofit2.Callback<List<DataDmsDetail5>> {




            override fun onFailure(call: Call<List<DataDmsDetail5>>, error: Throwable) {
                Log.d("onResponse", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataDmsDetail5>>,
                response: Response<List<DataDmsDetail5>>
            ) {
//                Log.d("5Reload" , "${keyword2}")
//                Log.d("5Reload" , "${keyword3}")
//                Log.d("5Reload" , "${keyword4}")
//                Log.d("5Reload" , "${keyword5}")

                val responseBody = response.body()!!

                Log.d("DMS DataDmsDetail5", "{$responseBody}")

                val items3 = mutableListOf<DataDmsDetail5>()
                for (dmsdetail5 in responseBody) {
                    //  binding.txtReq1D.text =  DataDmsDetail4.reqId
                    var vapprDate: String = ""
                    if(dmsdetail5.apprDate == null || dmsdetail5.apprDate =="" ) {
                        vapprDate = " "
                    }else{
                        vapprDate = dmsdetail5.apprDate
                    }

                    var vappUserId: String = ""
                    if(dmsdetail5.apprUserId == null || dmsdetail5.apprUserId =="" ) {
                        vappUserId = " "
                    }else{
                        vappUserId =  dmsdetail5.apprUserId
                    }


                    var vapprCmmt: String = ""
                    if(dmsdetail5.apprCmmt == null || dmsdetail5.apprCmmt =="" ) {
                        vapprCmmt = " "
                    }else{
                        vapprCmmt =  dmsdetail5.apprCmmt
                    }
//
//                    Log.d("vapprCmmt1", "11111")
//                    Log.d("vapprCmmt1", "${vapprCmmt}")
//                    Log.d("vapprCmmt1", "2222")
                    //apprTyp = dmsdetail5.apprTyp

                    items3.add(
                        DataDmsDetail5(
                            dmsdetail5.apprOrgTyp,
                            dmsdetail5.apprTypNm,
                            dmsdetail5.apprEmpNm,
                            vapprDate,
                            dmsdetail5.apprCfmFgNm,
                            dmsdetail5.myTurn,
                            vappUserId,
                            vapprCmmt,
                            ReqNo,
                            RevNo,
                            apprSeq,
                            dmsdetail5.apprTyp

                        )
                    )

                }


                    val adapter = ApprListViewAdapter(SubActivity(),items3)
                    binding.listView.adapter = adapter


                binding.listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
                        val item = parent.getItemAtPosition(position)
                        createBottomSheet(position ,  items3 )
                }


            }

        })
    }


    private fun requestGet1(service: SubActivity.RetrofitService, keyword1:String, keyword2:String, keyword3:String){
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

    private fun showLoadingDialog() {
        val dialog = LoadingDialog(this )
        CoroutineScope(Dispatchers.Main).launch {
            dialog.show()
            delay(1000)
            dialog.dismiss()
            // button.text = "Finished"
        }
    }


    fun Spinner.selected(action: (position:Int) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                action(position)
            }
        }
    }


    fun removeTag(str: String): String {
        var str = str
        str = Normalizer.normalize(str, Normalizer.Form.NFKC)
        var mat: Matcher

        // script 처리
        val script = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL)
        mat = script.matcher(str)
        str = mat.replaceAll("")

        // style 처리
        val style = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL)
        mat = style.matcher(str)
        str = mat.replaceAll("")

        // tag 처리
        val tag = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>")
        mat = tag.matcher(str)
        str = mat.replaceAll("")

        // ntag 처리
        val ntag = Pattern.compile("<\\w+\\s+[^<]*\\s*>")
        mat = ntag.matcher(str)
        str = mat.replaceAll("")

        // entity ref 처리
        val Eentity = Pattern.compile("&[^;]+;")
        mat = Eentity.matcher(str)
        str = mat.replaceAll("")

        // whitespace 처리
        val wspace = Pattern.compile("\\s\\s+")
        mat = wspace.matcher(str)
        str = mat.replaceAll("")
        return str
    }



}