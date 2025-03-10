package com.example.daisoworks



import android.R
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.adapter.ExpandableAdapterMyList
import com.example.daisoworks.data.DataExhPartner
import com.example.daisoworks.data.DataExhPartnerCount
import com.example.daisoworks.data.ExhibitionList
import com.example.daisoworks.data.ExhibitionMyList
import com.example.daisoworks.databinding.ActivityExhibitionBinding
import com.example.daisoworks.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ExhibitionActivity : AppCompatActivity() {

    companion object{
        lateinit var prefs: PreferenceUtil
        private const val TAG = "ExhibitionActivity"
    }

    // 전역 변수로 바인딩 객체 선언
    private var mbinding: ActivityExhibitionBinding? = null



    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mbinding!!


    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService
    //spinner 값 배열리스트 초기화
    //배열 초기화
    private var languageList = ArrayList<ExhibitionMyList>()
    private lateinit var expandableAdapterExhMyList: ExpandableAdapterMyList


    private lateinit var kselectedValue: String

    //spinner의 값을 가지고 위의 배열에서 바이어시디코드를 저장함.
    private   var exhListCd : String = ""

    private var exhSdate : String = ""     // 전시회상담조회시작일자
    private var exhEdate : String = ""     // 전시회상담조회종료일자
    private var memempmgnum : String = "" // 상담자
    private var vcompanycode : String = ""
    private var id1 : String = ""

    private var exhSelCode : String = ""  // 전시회코드
    private var dataexhpartnercount = mutableListOf<DataExhPartnerCount>()
    private var partnerDialog1 = mutableListOf<String>()
    private var partnerEmpNo : String = "" // 동반자 번호



    //spinner 값 배열리스트 초기화
    private val exhListDataArr = mutableListOf<String>("전시회 선택")
    //spinner는 id,value 형태가 아니라서 value만 들어감. 그래서 Map형태로 API통신시 미리 두개를 가져오(한개는 spinner값, 검색버튼 누를때 spinner값을 코드로 치환해주기 위한 Map임.
    private var mutableexhMap = mutableMapOf<String, String>()



    private lateinit var autoexhbitsb: Switch
    private var isSwitchOn: Boolean = false


    private var comSel: String = ""
    private var memdeptcde : String = "" // 상담자부서코드

    private lateinit var atextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // setContentView(R.layout.activity_exhibition)

        prefs = PreferenceUtil(applicationContext)
        // 1. 바인딩 초기화
        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mbinding = ActivityExhibitionBinding.inflate(layoutInflater)


        kselectedValue = " "

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        //기본 Actionbar 제목 변경
        getSupportActionBar()?.setTitle("전시회 상담관리")
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)

        memempmgnum =  prefs.getString("memempmgnum", "none")
        memdeptcde =  prefs.getString("memdeptcde", "none")



        val now: LocalDateTime = LocalDateTime.now()
        val vdateFormat = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val cal = Calendar.getInstance()

        var sevendays = (now.plusDays(-6)).format(formatter).toString()

       binding.txtcousdate.setText(sevendays.format(formatter).toString())
       binding.txtcouedate.setText(now.format(formatter).toString())


        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit =  RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        //API서비스 호출 파라미터
        getExhList(supplementService,"${BuildConfig.API_KEY}")



        autoexhbitsb = binding.autoexhbit

         var autoexhFlagS = prefs.getString("autoexhSet","N")
        Log.d("autoexhFlagS" , "${autoexhFlagS}")
        if(autoexhFlagS=="Y") {
            autoexhbitsb.isChecked = true
        }else{
            autoexhbitsb.isChecked = false
        }


        // 스위치 버튼 상태 변경 리스너
        autoexhbitsb.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                isSwitchOn = true  // 상태 저장

                autoexhbitsb.thumbTintList = ColorStateList.valueOf(Color.WHITE)


                showBottomSheet()

            } else {

                isSwitchOn = false
                prefs.setString("autoexhSet", "N")
                prefs.setString("autoexhSetItem", "")
                prefs.setString("autoexhSetDate", "")
                prefs.setString("autoexhPartnerEmpNo", "")
                prefs.setString("autoexhKselectedValue", "")

            }
        }





        binding.txtcousdate.setOnClickListener{
            keyBordHide()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->

                var mmonth = ""
                var dday = ""

                if(m < 10){
                    mmonth = "0" + (m+1)
                }else{
                    mmonth = ""+(m+1)
                }

                if(d < 10){
                    dday = "0" + (d)
                }else{
                    dday = ""+(d)
                }


                binding.txtcousdate.setText("$y-${mmonth}-$dday")
                exhSdate  =  "$y-${mmonth}-$dday"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

//        binding.txtcousdate.setOnFocusChangeListener{ view, b ->
//
//         }
        binding.txtcouedate.setOnClickListener{
            keyBordHide()
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->

                var mmonth = ""
                var dday = ""

                if(m < 10){
                    mmonth = "0" + (m+1)
                }else{
                    mmonth = ""+(m+1)
                }

                if(d < 10){
                    dday = "0" + (d)
                }else{
                    dday = ""+(d)
                }


                binding.txtcouedate.setText("$y-${mmonth}-$dday")
                exhEdate  =  "$y-${mmonth}-$dday"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }




        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit =  RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)
        memempmgnum =  prefs.getString("memempmgnum", "none")
        vcompanycode =  prefs.getString("companycode", "none")
        id1 =  prefs.getString("id", "none")
        // 아성다이소이면 HERP empnum이 없어서 그냥 HR 사번에 앞에 회사코드 두자리 뺴고 저장함.
        if(vcompanycode=="10005"  || memempmgnum=="" || memempmgnum == null ||  memempmgnum.equals("0") ){
            memempmgnum = id1.substring(2)
        }

        //schsdate schedate empno
        var psdate = binding.txtcousdate.text
        var pedate = binding.txtcouedate.text
        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        //Log.d("testtestset" , "${memempmgnum}")

        getExhMyList(supplementService,"${psdate}","${pedate}","${memempmgnum}","${BuildConfig.API_KEY}")



        binding.exhSchRegist.setOnClickListener{
            keyBordHide()
            //schsdate schedate empno
            var psdate = binding.txtcousdate.text
            var pedate = binding.txtcouedate.text
            //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
            getExhMyList(supplementService,"${psdate}","${pedate}","${memempmgnum}","${BuildConfig.API_KEY}")
        }
       // val sbtn = findViewById<Button>(R.id.exhRegist)
        binding.homeGoalAddFloatingBtn.setOnClickListener {
            //Log.d("testtestset" , "click")
            //  val id = etId.text.toString()
            val intent = Intent(this, ExhibitionWriteActivity::class.java)

            //  intent.putExtra("id", id)
            startActivity(intent)
        }




    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
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


    interface RetrofitService {
        @GET("exhMyList")
        fun exhibitionMyList(
            @Query("schsdate") param1: String,
            @Query("schedate") param2: String,
            @Query("empno") param3: String,
            @Query("apikey") param5: String
        ): Call<List<ExhibitionMyList>>

        @GET("exhList")
        fun exhibitionList(
            @Query("apikey") param1: String
        ): Call<List<ExhibitionList>>

        @GET("exhPartner")
        fun exhpartner(
            @Query("comCode") param1: String,
            @Query("uname") param2: String,
            @Query("apikey") param3: String
        ): Call<List<DataExhPartner>>

    }

//in  : memempmgnum , exhDate , apikey
//out : tclntConNo, exhNum , exhComName ,exhDate

    private fun showBottomSheet() {


        // BottomSheetDialog 초기화
        val bottomSheetDialog = BottomSheetDialog(this)

        // BottomSheet의 View Binding 초기화
        val bottomSheetBinding = BottomSheetLayoutBinding.inflate(layoutInflater)


        bottomSheetDialog.setContentView(bottomSheetBinding.root)


        var cal = Calendar.getInstance()


        bottomSheetBinding.exhRegistcal.setOnClickListener {
            keyBordHide()
            Log.d("ddddd" , "ddddd")
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->

                var mmonth = ""
                var dday = ""

                if(m < 10){
                    mmonth = "0" + (m+1)
                }else{
                    mmonth = ""+(m+1)
                }

                if(d < 10){
                    dday = "0" + (d)
                }else{
                    dday = ""+(d)
                }


                bottomSheetBinding.txtcousdate.text = "$y-${mmonth}-$dday".toEditable()
                //  exhDate  =  "$y-${mmonth}-$dday"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        // 바텀시트 닫힐 때 리스너 설정
        bottomSheetDialog.setOnDismissListener {
            // 스위치 버튼 상태 유지
            autoexhbitsb.isChecked = isSwitchOn
        }

        bottomSheetDialog.show()




        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정하여 줍니다.
        val adapter = ArrayAdapter(this,
            com.example.daisoworks.R.layout.spinnerlayout, exhListDataArr)

        //입력된 spinner에 어댑터를 연결한다.
        val spinner = bottomSheetBinding.splistexhibition1
      //  Log.d("spineer" , "${spinner}")
        spinner.adapter = adapter



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //  textView.text = "선택됨: $position ${spinner.getItemAtPosition(position)}"
                //  Log.d("testtestset" , "선택됨: $position ${spinner.getItemAtPosition(position)}")

                val selected = exhListDataArr.get(position)

                fun <K, V> getKey(map: Map<K, V>, target: V): K? {
                    for ((key, value) in map)
                    {
                        if (target == value) {
                            return key
                        }
                    }
                    return null
                }
                var test1 = getKey(mutableexhMap, selected).toString()
                exhSelCode = test1
                // Log.d("testtestset1" , " ${test1}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }



        val list01 = arrayOf("회사선택","HS", "AH", "AS", "AD")

        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정하여 줍니다.
        val adapter1 = ArrayAdapter(this, com.example.daisoworks.R.layout.spinnerlayout, list01)


        // val spinner = findViewById<Spinner>(R.id.splistexhibition)
        bottomSheetBinding.spexhCom.adapter = adapter1


        bottomSheetBinding.exhUserSearch.setOnClickListener {

                if(comSel == "회사선택" || comSel == "") {
                    Toast.makeText( this,"동반자의 회사를 선택하세요", Toast.LENGTH_SHORT).show()
                }else{
                    var kcomcd=""
                    if(comSel=="HS") {
                        kcomcd = "77777"
                    }else if(comSel=="AH") {
                        kcomcd="10000"
                    }else if(comSel=="AS") {
                        kcomcd="00001"
                    }else if(comSel=="AD") {
                        kcomcd="10005"
                    }
                    var kstr1 = bottomSheetBinding.txtpartUser.text.toString()

                    if (kstr1.equals("null")){
                        showAlert1("동반자명을 입력하세요")

                    } else if (kstr1.length < 2 ){
                        showAlert1("검색어를 2글자이상 입력하세요")
                    }else {
                        kstr1 =   "%"+kstr1+"%"
                        getexhpartner(supplementService, kcomcd, kstr1,"${BuildConfig.API_KEY}", bottomSheetBinding)

                        bottomSheetBinding.txtpartTotal1.setText("${kselectedValue}")


                    }
                }

            }


        bottomSheetBinding.exhUserSet.setOnClickListener {

            val  autoexhdate = bottomSheetBinding.txtcousdate.text
            prefs.setString("autoexhSet", "Y")
            prefs.setString("autoexhSetItem", "${exhSelCode}")
            prefs.setString("autoexhSetDate", "${autoexhdate}")
            prefs.setString("autoexhPartnerEmpNo", "${partnerEmpNo}")
            prefs.setString("autoexhKselectedValue", "${kselectedValue}")

            bottomSheetDialog.dismiss()


        }

        bottomSheetBinding.spexhCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //  textView.text = "선택됨: $position ${binding.spexhCom.getItemAtPosition(position)}"
                Log.d("testtestset" , "선택됨: $position ${bottomSheetBinding.spexhCom.getItemAtPosition(position)}")

                var kint: String = bottomSheetBinding.spexhCom.getItemAtPosition(position).toString()
                comSel = kint

               // Log.d("testtestset" , "${comSel}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }



    private fun getexhpartner(
        service: RetrofitService,
        keyword1: String,
        keyword2: String,
        keyword3: String,
        bottomSheetBinding: BottomSheetLayoutBinding
    ){
        service.exhpartner(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataExhPartner>> {
            override  fun onFailure(call: Call<List<DataExhPartner>>, error: Throwable) {
                Log.d("DataExhPartner", "실패원인: {$error}")
            }
            override fun onResponse(
                call: Call<List<DataExhPartner>>,
                response: Response<List<DataExhPartner>>
            ) {
                // BuyerCd = keyword2
                Log.d("DataExhPartner", "DataExhPartner API 성공 ")
                val responseBody = response.body()!!

                //val bottomSheetBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
                // BottomSheetDialog 초기화

                //    partnercount partnerDialog1
                dataexhpartnercount = mutableListOf<DataExhPartnerCount>()
                partnerDialog1 = mutableListOf<String>()
                for(partnercnt1 in responseBody) {
                    dataexhpartnercount.add( DataExhPartnerCount(partnercnt1.nme , partnercnt1.hnme))
                    var ktitle: String = ""
                    var kcomcd1=""
                    if(partnercnt1.corpcd=="77777") {
                        kcomcd1 = "HS"
                    }else if(partnercnt1.corpcd=="10000") {
                        kcomcd1="AH"
                    }else if(partnercnt1.corpcd=="00001") {
                        kcomcd1="AS"
                    }else if(partnercnt1.corpcd=="10005") {
                        kcomcd1="AD"
                    }
                    ktitle = "["+kcomcd1+"] " + partnercnt1.hnme + "(" + partnercnt1.nme+")"

                    partnerDialog1.add(ktitle)
                }
                Log.d("dataexhpartnercount", dataexhpartnercount.size.toString())
                if(dataexhpartnercount.size > 1) {
                    createDialog(partnerDialog1) { selectedValue ->
                        // 다이얼로그에서 선택한 값을 바텀시트 TextView에 반영
                        bottomSheetBinding.txtpartTotal1.text = "${selectedValue}"
                        bottomSheetBinding.txtpartTotal1.gravity = Gravity.LEFT
                        bottomSheetBinding.txtpartTotal1.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
                        kselectedValue = selectedValue
                        Log.d("testtest11", "${selectedValue}")
                        keyBordHide()
                    }

                }else if(dataexhpartnercount.size == 1) { // 가 1개일 경우
                    createDialog(partnerDialog1) { selectedValue ->
                        // 다이얼로그에서 선택한 값을 바텀시트 TextView에 반영
                        bottomSheetBinding.txtpartTotal1.text = "${selectedValue}"
                        bottomSheetBinding.txtpartTotal1.gravity = Gravity.LEFT
                        bottomSheetBinding.txtpartTotal1.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START

                        kselectedValue = selectedValue
                        Log.d("kselectedValue", "${kselectedValue}")
                        keyBordHide()
                    }

                }else {
                    showAlert1("데이터가 존재하지 않습니다.")
                }




            }
        })
    }

    private fun showAlert1( str1:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림")
        builder.setMessage(str1)
        builder.show()
    }
    fun createDialog(pparnercount: MutableList<String>, onItemClick: (String) -> Unit){

        val dataList = pparnercount

       // val bottomSheetBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
        // BottomSheetDialog 초기화
     //   val bottomSheetDialog = BottomSheetDialog(this)
        val adapter = ArrayAdapter<String>(
            this, android.R.layout.select_dialog_singlechoice ,dataList
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("동행자 선택")
        // builder에 어뎁터 설정
        // 두 번째 매개변수에는 사용자가 선택한 항목의 순서값이 들어온다.

//        builder.setAdapter(adapter) { _, which ->
//            val selectedItem = dataList[which] // 선택된 데이터 가져오기
//            onItemClick(selectedItem) // 선택한 값을 콜백으로 전달
//        }

        builder.setAdapter(adapter){ dialogInterface: DialogInterface, i: Int ->
           // setTextView.text = "선택한 항목 : ${dataList[i]}"
            // BottomSheet의 View Binding 초기화
            kselectedValue = dataList[i] // 선택한 데이터 가져오기
            onItemClick(kselectedValue) // 콜백 호출


            val schstr =  dataList[i]
            val searchkey1 = "("
            val searchkey2 = ")"

            val indexOf1 = schstr.indexOf(searchkey1)+1
            val indexOf2 = schstr.indexOf(searchkey2)

            partnerEmpNo = schstr.substring(indexOf1, indexOf2)
             //Log.d("testtest" , "${partnerEmpNo}")
            keyBordHide()
        }


        builder.setNegativeButton("취소",null)
        builder.show()

    }


    private fun getExhList(service: RetrofitService, keyword1:String){
        service.exhibitionList(keyword1).enqueue(object: Callback<List<ExhibitionList>> {
            override  fun onFailure(call: Call<List<ExhibitionList>>, error: Throwable) {
                Log.d("ExhibitionList", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<ExhibitionList>>,
                response: Response<List<ExhibitionList>>
            ) {
                val responseBody = response.body()!!

                Log.d("exhListData1" , "{$responseBody.size}")

                //val myStringBuilder = StringBuilder()
                exhListDataArr.clear()
                mutableexhMap.clear()
                exhListDataArr.add("전시회 선택")
                for(exhListData1 in responseBody) {
                    exhListDataArr.add( exhListData1.cname)
                    mutableexhMap.put(exhListData1.ckey , exhListData1.cname)

                }
            }
        })
    }

    private fun getExhMyList(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String){
        service.exhibitionMyList(keyword1,keyword2,keyword3,keyword4).enqueue(object: Callback<List<ExhibitionMyList>> {
            override  fun onFailure(call: Call<List<ExhibitionMyList>>, error: Throwable) {
                Log.d("ExhibitionMyList", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<ExhibitionMyList>>,
                response: Response<List<ExhibitionMyList>>
            ) {
                val responseBody = response.body()!!
                languageList.clear()
                Log.d("ExhibitionMyList", "testest")
                for (comCdData1 in responseBody) {

                    languageList.add(ExhibitionMyList(comCdData1.tclntConNo, comCdData1.exhDate, comCdData1.exhComName, comCdData1.exhNum)) // CorpId에 필요한 값을 전달*/
                    Log.d("ExhibitionMyList", ": ${comCdData1.tclntConNo}")
                }



                expandableAdapterExhMyList = ExpandableAdapterMyList(languageList)
                binding.root.findViewById<RecyclerView>(com.example.daisoworks.R.id.rv_exhlist).adapter = expandableAdapterExhMyList


                expandableAdapterExhMyList.setItemClickListener(object: ExpandableAdapterMyList.OnItemClickListener{
                    override fun onClick(v: View, position: Int) {
                        // 클릭 시 이벤트 작성
//                        Toast.makeText(this@ExhibitionActivity ,
//                            "${languageList[position].tclntConNo}\n${languageList[position].exhNum}",
//                            Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@ExhibitionActivity, ExhibitionUpdateActivity::class.java)
                        intent.putExtra("Data1", "${languageList[position].tclntConNo}")
                        startActivity(intent)


                    }
                })
            }
        })
    }





    private fun keyBordHide() {
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    }


    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mbinding = null
        super.onDestroy()
    }
    override fun onResume() {
        super.onResume()
        // 액티비티가 재개될 때 수행할 작업
       // Log.d("ActivityLifecycle", "Activity resumed")
        var psdate = binding.txtcousdate.text
        var pedate = binding.txtcouedate.text
        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getExhMyList(supplementService,"${psdate}","${pedate}","${memempmgnum}","${BuildConfig.API_KEY}")
    }

}