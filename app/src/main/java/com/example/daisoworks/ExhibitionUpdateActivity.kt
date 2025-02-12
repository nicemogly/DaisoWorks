package com.example.daisoworks

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.daisoworks.adapter.ImageAdapter
import com.example.daisoworks.data.AutoNum
import com.example.daisoworks.data.ClientCount
import com.example.daisoworks.data.DataClientSchDetail1
import com.example.daisoworks.data.DataExhPartner
import com.example.daisoworks.data.DataExhPartner1
import com.example.daisoworks.data.DataExhPartnerCount
import com.example.daisoworks.data.DataExhPartnerCount1
import com.example.daisoworks.data.ExhCounselNum
import com.example.daisoworks.data.ExhibitionDetail
import com.example.daisoworks.data.ExhibitionList
import com.example.daisoworks.data.ExhibitionUpdate
import com.example.daisoworks.data.apirstData
import com.example.daisoworks.databinding.ActivityExhibitionUpdateBinding
import com.example.daisoworks.util.LoadingDialog
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ExhibitionUpdateActivity : AppCompatActivity() {


    companion object{
        lateinit var prefs: PreferenceUtil
        private const val TAG = "ExhibitionUpdateActivity"
    }
    // 전역 변수로 바인딩 객체 선언
    private var mbinding: ActivityExhibitionUpdateBinding? = null
    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() = mbinding!!

    private lateinit var uri: Uri
    //lateinit var exhibitionActivity: ExhibitionActivity
    private var uriList = ArrayList<Uri>()
    private val maxNumber = 10
    lateinit var adapter: ImageAdapter


    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : ExhibitionUpdateActivity.RetrofitService
    private var ClientcodeP : String = ""
    private var ClientnameK : String = ""

    private var data9 : MutableList<String>   = mutableListOf()
    //spinner 값 배열리스트 초기화
    private val exhListDataArr = mutableListOf<String>("전시회 선택")
    //spinner는 id,value 형태가 아니라서 value만 들어감. 그래서 Map형태로 API통신시 미리 두개를 가져오(한개는 spinner값, 검색버튼 누를때 spinner값을 코드로 치환해주기 위한 Map임.
    private var mutableexhMap = mutableMapOf<String, String>()

    private var comSel: String = ""

    private var dataexhpartnercount = mutableListOf<DataExhPartnerCount>()
    private var dataexhpartnercount1 = mutableListOf<DataExhPartnerCount1>()

    private var partnerDialog1 = mutableListOf<String>()
    private var Partnernme : String = ""
    private var Partnerhnme : String = ""
    private var Partnercorpcd : String = ""


    private var clientcount = mutableListOf<ClientCount>()
    private var clientDialog1 = mutableListOf<String>()

    private var id1 : String = ""
    private var vcompanycode : String = ""


    private var memhnme : String = ""

    // Regist Filed
    private var exhSelCode : String = ""  // 전시회코드
    private var exhDate : String = ""     // 전시회상담일자
    private var memempmgnum : String = "" // 상담자
    private var comCd : String = ""      // 상담자 회사
    private var partnerEmpNo : String = "" // 동반자 번호
    private var exhNum : String = ""     // 상담일지코드번호
    private var exhComName : String   = ""      // 상담업체명
    private var exhSampleCnt : Int   = 0       // 상담업체 샘플수
    private var exhSangdamCnt : Int  = 0      // 상담일지장수
    private var exhSampleRtnYN : String = "1"   // 샘플반송여부
    private var vautonum : String = ""  // API를 통해 가져오 오토발번코드



    private var kurl1: String  = ""
    private var kurl2: String  = ""
    private var kurl3: String  = ""
    private var vseq: Int  = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exhibition_update)
        prefs = PreferenceUtil(applicationContext)


        // 1. 바인딩 초기화
        mbinding = ActivityExhibitionUpdateBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)

        //로그인시 담아놓은 회사코드를 가지고  API통신시 파라미터값으로 활용함.
        comCd = prefs.getString("companycode","0")
        vcompanycode = prefs.getString("companycode", "none")

        id1 =  prefs.getString("id", "none")
        memempmgnum =  prefs.getString("memempmgnum", "none")
        memhnme =  prefs.getString("memhnme", "none")

        //기본 Actionbar 제목 변경
        getSupportActionBar()?.setTitle("전시회 상담수정")
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit =  RetrofitClient.getInstance()
        supplementService = retrofit.create(ExhibitionUpdateActivity.RetrofitService::class.java)



       // clearCache(this@ExhibitionUpdateActivity)
        val handlerThread = HandlerThread("BackgroundThread")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        handler.post {
            // 백그라운드 작업 수행
            clearCache(this@ExhibitionUpdateActivity)
        }

        val kattr1 = intent.getStringExtra("Data1").toString()
        if(kattr1==""){
            showAlert1("정상적인 접근이 아닙니다.")
            finish()
        }else{
            getExhUpdate(supplementService, kattr1,"${BuildConfig.API_KEY}")
        }






        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정하여 줍니다.
        val adapter = ArrayAdapter(this,R.layout.spinnerlayout, exhListDataArr)

        //activity_main.xml에 입력된 spinner에 어댑터를 연결한다.
        val spinner = findViewById<Spinner>(R.id.splistexhibition)
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


        val cal = Calendar.getInstance()

        // 달력 로드
        binding.exhRegistcal.setOnClickListener {
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


                binding.txtcousdate.text = "$y-${mmonth}-$dday".toEditable()
                exhDate  =  "$y-${mmonth}-$dday"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }


        var totname = memhnme + "("+id1+"/"+memempmgnum+")"


        binding.txtcousTotal.setText(totname)
        binding.txtcoussabun.setText(id1)
        binding.txtcousempno.setText(memempmgnum)


        val list01 = arrayOf("회사선택","HS", "AH", "AS", "AD")

        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정하여 줍니다.
        val adapter1 = ArrayAdapter(this,R.layout.spinnerlayout, list01)


        // val spinner = findViewById<Spinner>(R.id.splistexhibition)
        binding.spexhCom.adapter = adapter1


        binding.exhUserSearch.setOnClickListener {

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
                var kstr1 = binding.txtpartUser.text.toString()
                kstr1 =   "%"+kstr1+"%"

                getexhpartner(supplementService, kcomcd, kstr1,"${BuildConfig.API_KEY}")

            }

        }


        binding.spexhCom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //  textView.text = "선택됨: $position ${binding.spexhCom.getItemAtPosition(position)}"
                Log.d("testtestset" , "선택됨: $position ${binding.spexhCom.getItemAtPosition(position)}")

                var kint: String = binding.spexhCom.getItemAtPosition(position).toString()
                comSel = kint

                Log.d("testtestset" , "${comSel}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.exhcodenum.setOnClickListener{
            var  kcalvalue = binding.txtcousdate.text.toString()
            // var  phsid =  prefs.getString("hsid","0")

            Log.d("testtest" ,"${exhSelCode}")
            if(exhSelCode.equals("null")){
                Toast.makeText( this,"전시회를 선택하세요", Toast.LENGTH_SHORT).show()
            }else if(kcalvalue == "") {
                Toast.makeText(this, "날짜를 선택하세요", Toast.LENGTH_SHORT).show()
            }else{
                //Toast.makeText(this, "발번 시작", Toast.LENGTH_SHORT).show()
                //API서비스 호출 파라미터  exhnum , hsid ,mdate
                Log.d("testtest_exhSelCode" ,"${exhSelCode}")
                //   Log.d("testtest_phsid" ,"${phsid}")
                Log.d("testtest_kcalvalue" ,"${kcalvalue}")
                getexhCounselNum(supplementService, exhSelCode , kcalvalue,"${BuildConfig.API_KEY}")
            }
        }


        binding.rgflag.setOnCheckedChangeListener { _, checkedId ->
            //if catButton was selected add 1 to variable cat
            if (checkedId == R.id.rbflag_1) {
                binding.txtschcomname.visibility = View.GONE
                binding.exhcomsearch.visibility = View.GONE
                // binding.txtcomname.requestFocus()
                binding.txtcomname.setText("")
                binding.txtcomname.isFocusable = true
                binding.txtcomname.isFocusableInTouchMode = true
                binding.txtcomname.setBackground(ContextCompat.getDrawable(this, R.drawable.box_border))

            }
            //if dogButton was selected add 1 to variable dog
            if (checkedId == R.id.rbflag_2) {
                binding.txtschcomname.visibility = View.VISIBLE
                binding.exhcomsearch.visibility = View.VISIBLE
                binding.txtcomname.isFocusable = false
                binding.txtcomname.setBackgroundColor(Color.parseColor("#f0f0f0"))

            }
        }

        binding.exhcomsearch.setOnClickListener{



            var  kschvalue = binding.txtschcomname.text.toString()
            if (kschvalue.equals("null")){
                showAlert1("거래처명을 입력하세요")

            } else if (kschvalue.length < 3 ){
                showAlert1("검색어를 2글자이상 입력하세요")
            }else {

                var kschvalue1 =   "%"+kschvalue+"%"
                Log.d("testtest" , "${comCd}")
                getClientSchCountList(supplementService, comCd, kschvalue1,"${BuildConfig.API_KEY}")
            }
        }



        binding.rgsamflag.setOnCheckedChangeListener { _, checkedId1 ->
            //if catButton was selected add 1 to variable cat
            if (checkedId1 == R.id.rbrtnflag_1) {
                exhSampleRtnYN = "Y"
            }
            //if dogButton was selected add 1 to variable dog
            if (checkedId1 == R.id.rbrtnflag_2) {
                exhSampleRtnYN = "N"
            }
        }


        // val adapter1 =
        this@ExhibitionUpdateActivity.adapter = ImageAdapter(this, uriList)
        binding.recyclerview.adapter =  this@ExhibitionUpdateActivity.adapter
        // LinearLayoutManager을 사용하여 수평으로 아이템을 배치한다.
        binding.recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)



        // ImageView를 클릭할 경우
        // 선택 가능한 이미지의 최대 개수를 초과하지 않았을 경우에만 앨범을 호출한다.
        binding.imageArea.setOnClickListener {
            if (uriList.count() == maxNumber) {
                Toast.makeText(
                    this,
                    "이미지는 최대 ${maxNumber}장까지 첨부할 수 있습니다.",
                    Toast.LENGTH_SHORT
                ).show();
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            registerForActivityResult.launch(intent)
        }





        // 업로드 하기 버튼을 클릭할 경우
        // for문을 통해 uriList.count()만큼 imageUpload 함수를 호출한다.
        binding.btnRegister.setOnClickListener {

            exhComName = binding.txtcomname.text.toString()
            exhSampleCnt = binding.txtsamplecnt.text.toString().toInt()
            exhSangdamCnt = binding.txtsangdamcnt.text.toString().toInt()

            //======================================================================================
            // 1.입력필드 값 Validation
            // 2.채번번호 가지고 오기
            // 3.전시회 상담 헤더 등록
            // 4.전시회 파일등록
            //======================================================================================

            // 1.입력필드 값 Validation
            // private var exhSelCode : String = ""  // 전시회코드
            // private var exhDate : String = ""     // 전시회상담일자
            // private var memempmgnum : String = "" // 상담자
            // private var comCd : String = ""      // 상담자 회사
            // private var partnerEmpNo : String = "" // 동반자 번호
            // private var exhNum : String = ""     // 상담일지코드번호
            // private var exhComName : String   = ""      // 상담업체명
            // private var exhSampleCnt : String   = ""       // 상담업체 샘플수
            // private var exhSangdamCnt : String  = ""      // 상담일지장수
            // private var exhSampleRtnYN : String = ""   // 샘플반송여부

            // 아성다이소이면 HERP empnum이 없어서 그냥 HR 사번에 앞에 회사코드 두자리 뺴고 저장함.
            if(vcompanycode=="10005" ){
                memempmgnum = id1.substring(2)
            }

            // 1.1 전시회코드 체크
            if(exhSelCode=="" || exhSelCode == null || exhSelCode.equals("null") ){
                showAlert("전시회 상담등록 오류" , "해당 전시회를 선택하세요", "확인" )
                // 1.2 전시회 상담일자 체크
            } else if(exhDate=="" || exhDate == null || exhDate.equals("null") ){
                showAlert("전시회 상담등록 오류" , "전시회 상담일자를 입력하세요", "확인" )
                // 1.3 전시회 상담자 체크
            } else if( memempmgnum==null  || memempmgnum=="null" || memempmgnum=="" ||  memempmgnum=="0" || memempmgnum.equals("0") ){
                showAlert("전시회 상담등록 오류" , "정상적인 접근 방법이 아닙니다. 시스템 관리자에게 문의 바랍니다.", "확인" )
                // 1.4 전시회 동반자 체크
            } else if(partnerEmpNo=="" || partnerEmpNo == null || partnerEmpNo.equals("null")  ){
                showAlert("전시회 상담등록 오류" , "동반자 정보를 선택하세요", "확인" )

                // 1.5 상담일지코드 체크
            } else if(exhNum=="" || exhNum == null || exhNum.equals("null")  ){
                showAlert("전시회 상담등록 오류" , "상담일지코드를 발번하세요.", "확인" )

                // 1.6 전시회 상담업체 체크
            } else if(exhComName=="" || exhComName == null || exhComName.equals("null")  ){
                showAlert("전시회 상담등록 오류" , "상담업체명을 입력하세요.", "확인" )

                // 1.7 전시회 상담샘플수 체크
            } else if(exhSampleCnt == null  ){
                showAlert("전시회 상담등록 오류" , "상담샘플수를 입력하세요.", "확인" )

                // 1.8 전시회 상담일지장수 체크
            } else if(exhSangdamCnt == null ){
                showAlert("전시회 상담등록 오류" , "상담일지장수를 입력하세요.", "확인" )

                // 1.9 전시회 샘플반송여부 체크
            } else if(exhSampleRtnYN=="" || exhSampleRtnYN=="0" || exhSampleRtnYN == null || exhSampleRtnYN.equals("null")  ){
                showAlert("전시회 상담등록 오류" , "샘플반송여부를 선택하세요.", "확인" )

            } else {

                // 2.채번번호 가지고 오기
                val now: LocalDateTime = LocalDateTime.now()
                val vdateFormat1 = vautonum.substring(2,8).toInt()
              //  showLoadingDialog()
               // getautoNum(supplementService, vdateFormat, "${BuildConfig.API_KEY}")

                var kkk = vautonum.substring(8,12)
                //var kkk = "0016"
                var kint = kkk.toInt()

                var suggbn = "300"


                var memempmgnum1 = memempmgnum
                var memempmgnum2 = memempmgnum
                var exhDate1 = exhDate

                var exhSampleRtnYN1: String = "1"
                if(exhSampleRtnYN=="Y"){
                    exhSampleRtnYN1 = "0"
                }else{
                    exhSampleRtnYN1 = "1"
                }

                exhDate = binding.txtcousdate.text.toString()


                val dialog = LoadingDialog(this)

                dialog.show()
                getexhUpdate1(supplementService,vautonum ,exhDate,vdateFormat1,kint,comCd,exhNum,exhSangdamCnt,exhSelCode,suggbn,memempmgnum,partnerEmpNo,exhComName,exhDate1,memempmgnum1,memempmgnum2,exhSampleRtnYN1,exhSampleCnt,"${BuildConfig.API_KEY}")

            }
        }


        //삭제 버튼 클릭시 실행
        binding.btnDelete.setOnClickListener {
            showConfirmDialog()
        }


    }

    fun showAlert(str1: String ,str2: String , str3: String ){

        val builder = AlertDialog.Builder(this)
        builder.setTitle(str1)
        builder.setMessage(str2)
        builder.setPositiveButton(str3, DialogInterface.OnClickListener { dialog, which ->
            //    Toast.makeText(this, str3, Toast.LENGTH_SHORT).show()
        })

        builder.create()
        builder.show()
    }



    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    val clipData = result.data?.clipData
                    if (clipData != null) { // 이미지를 여러 개 선택할 경우
                        val clipDataSize = clipData.itemCount
                        val selectableCount = maxNumber - uriList.count()
                        if (clipDataSize > selectableCount) { // 최대 선택 가능한 개수를 초과해서 선택한 경우
                            Toast.makeText(
                                this,
                                "이미지는 최대 ${selectableCount}장까지 첨부할 수 있습니다.",
                                Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            // 선택 가능한 경우 ArrayList에 가져온 uri를 넣어준다.
                            for (i in 0 until clipDataSize) {
                                uriList.add(clipData.getItemAt(i).uri)
                            }
                        }
                    } else { // 이미지를 한 개만 선택할 경우 null이 올 수 있다.
                        val uri = result?.data?.data
                        if (uri != null) {
                            uriList.add(uri)
                        }
                    }
                    // notifyDataSetChanged()를 호출하여 adapter에게 값이 변경 되었음을 알려준다.
                    adapter.notifyDataSetChanged()
                    printCount()
                }
            }
        }


    // 절대경로 변환
    fun absolutelyPath(path: Uri?, context : Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)

        return result!!
    }


    private fun printCount() {
        val text = "${uriList.count()}/${maxNumber}"
        binding.countArea.text = text
    }

    // 파일 업로드
    // 파일을 가리키는 참조를 생성한 후 putFile에 이미지 파일 uri를 넣어 파일을 업로드한다.
    private fun imageUpload(uri: Uri, count: Int) {
//        // storage 인스턴스 생성
//        val storage = Firebase.storage
//        // storage 참조
//        val storageRef = storage.getReference("image")
//        // storage에 저장할 파일명 선언
//        val fileName = SimpleDateFormat("yyyyMMddHHmmss_${count}").format(Date())
//
//        val mountainsRef = storageRef.child("${fileName}.png")
//        val uploadTask = mountainsRef.putFile(uri)
//
//        uploadTask.addOnSuccessListener { taskSnapshot ->
//            // 파일 업로드 성공
//            Toast.makeText(getActivity(), "사진 업로드 성공", Toast.LENGTH_SHORT).show();
//        }.addOnFailureListener {
//            // 파일 업로드 실패
//            Toast.makeText(getActivity(), "사진 업로드 실패", Toast.LENGTH_SHORT).show();
//        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            android.R.id.home -> {
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
        @GET("exhList")
        fun exhibitionList(
            @Query("apikey") param1: String
        ): Call<List<ExhibitionList>>

        @GET("exhDetail")
        fun exhibitionDetail(
            @Query("vautonum") param1: String,
            @Query("apikey") param2: String
        ): Call<List<ExhibitionDetail>>

        @GET("exhdelete")
        fun exhibitionDelete(
            @Query("vautonum") param1: String,
            @Query("vseq") param2: Int,
            @Query("apikey") param3: String
        ): Call<String>


        @GET("exhPartner")
        fun exhpartner(
            @Query("comCode") param1: String,
            @Query("uname") param2: String,
            @Query("apikey") param3: String
        ): Call<List<DataExhPartner>>

        @GET("exhPartner1")
        fun exhpartner1(
            @Query("empno") param1: String,
            @Query("apikey") param2: String
        ): Call<List<DataExhPartner1>>

        @GET("exhCounselNum")
        fun exhcounselnum(
            @Query("exhnum") param1: String,
            @Query("mdate") param3: String,
            @Query("apikey") param4: String
        ): Call<List<ExhCounselNum>>


        @GET("comview11")
        fun comView11(
            @Query("comCode") param1: String,
            @Query("comNum") param2: String,
            @Query("apikey") param3: String
        ): Call<List<DataClientSchDetail1>>

        @GET("autonum")
        fun autoNum(
            @Query("yyyymm") param1: String,
            @Query("apikey") param2: String
        ): Call<List<AutoNum>>

        @POST("exhupdate1")
        fun exhUpdate1(
            @Query("vautonum") param1: String,  // vautonum
            @Query("exhDate") param2: String,  // exhDate
            @Query("vdateFormat1") param3: Int,  // vdateFormat1
            @Query("kint1") param4: Int,  // kint
            @Query("comCd") param5: String,  // comCd
            @Query("exhNum") param6: String,  // exhNum
            @Query("exhSangdamCnt") param7: Int,  // exhSangdamCnt
            @Query("exhSelCode") param8: String,  // exhSelCode
            @Query("suggbn") param9: String,  // suggbn
            @Query("memempmgnum") param10: String,  // memempmgnum
            @Query("partnerEmpNo") param11: String,  // partnerEmpNo
            @Query("exhComName") param12: String,  // exhComName
            @Query("exhDate1") param13: String,  // exhDate
            @Query("memempmgnum1") param14: String,  // memempmgnum
            @Query("memempmgnum2") param15: String,  // memempmgnum
            @Query("exhSampleRtnYN1") param16: String,  // exhSampleRtnYN
            @Query("exhSampleCnt") param17: Int,  // exhSampleCnt
            @Query("apikey") param18: String
        ): Call<String>

        @GET("exhupdate")
        fun exhibitionUpdate(
            @Query("vautonum") param1: String,
            @Query("apikey") param2: String
        ): Call<List<ExhibitionUpdate>>



        @GET("imgdownload")
        fun imgView1(
            @Query("apikey") param1: String,
            @Query("reqno") param2: String,
            @Query("imgUrl") param3: String?
        ): Call<List<apirstData>>



        @Multipart
        @POST("/db/postImg")
        fun postImg(@Part photo: MultipartBody.Part, @PartMap params: Map<String, @JvmSuppressWildcards RequestBody> ): Call<String>
    }

    private fun getpostImg(service: RetrofitService, keyword1: MultipartBody.Part, keyword2: HashMap<String, RequestBody> ){
        service.postImg(keyword1,keyword2).enqueue(object: retrofit2.Callback<String> {


            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText( this@ExhibitionUpdateActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                finish()

            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@ExhibitionUpdateActivity, "실패했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

//    private fun getautoNum(service: RetrofitService, keyword1:String,keyword2:String){
//        service.autoNum(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<AutoNum>> {
//            override  fun onFailure(call: Call<List<AutoNum>>, error: Throwable) {
//                Log.d("ExhCounselNum", "실패원인: {$error}")
//            }
//
//            //Retrofit error 없이 Response 떨어지면
//            override fun onResponse(
//                call: Call<List<AutoNum>>,
//                response: Response<List<AutoNum>>
//            ) {
//                val responseBody = response.body()!!
//
//                vautonum =  responseBody[0].autonum
//
//                //   showLoadingDialog()
//
//                val now1: LocalDateTime = LocalDateTime.now()
//                val vdateFormat1 = now1.format(DateTimeFormatter.ofPattern("yyyyMM")).toInt()
//                //  Log.d("testtest", "${vautonum}")
//                var kkk = vautonum.substring(8,12)
//                //var kkk = "0016"
//                var kint = kkk.toInt()
//                //Log.d("testtest", "${kint}")
//                var suggbn = "300"
//
//                var exhSampleRtnYN1: String = "1"
//                if(exhSampleRtnYN=="Y"){
//                    exhSampleRtnYN1 = "0"
//                }else{
//                    exhSampleRtnYN1 = "1"
//                }
//
//                var memempmgnum1 = memempmgnum
//                var memempmgnum2 = memempmgnum
//                var exhDate1 = exhDate
//                // 3.전시회 상담 헤더 등록
//                //getautoNum(supplementService, vdateFormat, "${BuildConfig.API_KEY}")
//               // getexhRegist(supplementService,vautonum ,exhDate,vdateFormat1,kint,comCd,exhNum,exhSangdamCnt,exhSelCode,suggbn,memempmgnum,partnerEmpNo,exhComName,exhDate1,memempmgnum1,memempmgnum2,exhSampleRtnYN1,"${BuildConfig.API_KEY}")
//                getexhRegist(supplementService,vautonum ,exhDate,vdateFormat1,kint,comCd,exhNum,exhSangdamCnt,exhSelCode,suggbn,memempmgnum,partnerEmpNo,exhComName,exhDate1,memempmgnum1,memempmgnum2,exhSampleRtnYN1,exhSampleCnt,"${BuildConfig.API_KEY}")
//
//            }
//        })
//    }

    private fun getexhUpdate1(service: RetrofitService, keyword1:String, keyword2:String, keyword3:Int, keyword4:Int, keyword5:String, keyword6:String, keyword7:Int, keyword8:String, keyword9:String, keyword10:String, keyword11:String, keyword12:String, keyword13:String, keyword14:String, keyword15:String, keyword16:String, keyword17:Int, keyword18:String){
        service.exhUpdate1(keyword1,keyword2,keyword3,keyword4,keyword5,keyword6,keyword7,keyword8,keyword9,keyword10,keyword11,keyword12,keyword13,keyword14,keyword15,keyword16,keyword17,keyword18).enqueue(object: retrofit2.Callback<String> {


            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(call: Call<String>, response: Response<String>) {




                if(uriList.count()==0){
                    Toast.makeText( this@ExhibitionUpdateActivity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }else {
//keyword4
                    for (i in 0 until uriList.count()) {
                        // imageUpload(uriList.get(i), i)
                        Log.d("uriList", "xxxxxxx")
                        var file =
                            File(absolutelyPath(uriList.get(i), this@ExhibitionUpdateActivity))
                        var requestFile =
                            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                        //var vext = MultipartBody.Part.createFormData("file", file.name, requestFile)

                        var kext = file.toString().split(".")
                        var k = i + 1 + vseq
                        var orifilename = ""
                        if (kext[1] == "heic") {
                            orifilename = vautonum + "_" + k + ".heic"
                        } else {
                            orifilename = vautonum + "_" + k + ".JPG"
                        }
                        Log.d("uriListff", "${orifilename}")
                        //  var body = MultipartBody.Part.createFormData("file", vautonum+"_"+k+".JPG", requestFile)
                        var body =
                            MultipartBody.Part.createFormData("file", orifilename, requestFile)

                        var vattr1 = vautonum;
                        var vattr22 = i + 1 + vseq;
                        var vattr2 = vattr22.toString()
                        var vattr3 = vautonum + "_" + vattr2;
                        var vattr4 = memempmgnum;
                        var vattr5 = memempmgnum;
                        ///IMAGES/CLNTCON/BCON/BN2023102387/2023/
                        val now1: LocalDateTime = LocalDateTime.now()
                        val vyyyy = now1.format(DateTimeFormatter.ofPattern("yyyy"))
                        val vmm = now1.format(DateTimeFormatter.ofPattern("MM"))

                        var vattr6 = "/IMAGES/CLNTCON/BCON/" + vyyyy + "/" + vmm + "/";
                        //var vattr7 = vattr6.replace("/" , "//")
                        var vattr7 = """\IMAGES\CLNTCON\BCON\""" + vyyyy + """\""" + vmm + """\""";
                        var vattr8 = "${BuildConfig.API_KEY}"

                        val content1RequestBody: RequestBody = vattr1.toPlainRequestBody()
                        val content2RequestBody: RequestBody = vattr2.toPlainRequestBody()
                        val content3RequestBody: RequestBody = vattr3.toPlainRequestBody()
                        val content4RequestBody: RequestBody = vattr4.toPlainRequestBody()
                        val content5RequestBody: RequestBody = vattr5.toPlainRequestBody()
                        val content6RequestBody: RequestBody = vattr6.toPlainRequestBody()
                        val content7RequestBody: RequestBody = vattr7.toPlainRequestBody()
                        val content8RequestBody: RequestBody = vattr8.toPlainRequestBody()

                        val textHashMap = hashMapOf<String, RequestBody>()
                        textHashMap["vattr1"] = content1RequestBody  // 자동발번번호
                        textHashMap["vattr2"] = content2RequestBody  // 시퀀스 번호
                        textHashMap["vattr3"] = content3RequestBody  // 파일명확장자제외
                        textHashMap["vattr4"] = content4RequestBody  // 등록자
                        textHashMap["vattr5"] = content5RequestBody  // 등록자
                        textHashMap["vattr6"] = content6RequestBody  // 일반경로
                        textHashMap["vattr7"] = content7RequestBody  // 윈도우 경로
                        textHashMap["apikey"] = content8RequestBody  // APIKEY

                        getpostImg(supplementService, body, textHashMap)

                        Log.d("uriList", "${uriList.get(i)}")
                        Log.d("uriList", "${file}")
                        try {
                            Thread.sleep(500)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }

                //  Toast.makeText( this@ExhibitionWriteActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                // val responseBody = response.body()!!
                //
                //   finish()

            }

            override fun onFailure(call: Call<String>, t: Throwable) {

                //   Log.d("testtest" , "${t.toString()}")
                Toast.makeText(this@ExhibitionUpdateActivity, "실패했습니다.1", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())
    private fun getexhCounselNum(service: RetrofitService, keyword1:String,keyword2:String,keyword3:String){
        service.exhcounselnum(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<ExhCounselNum>> {
            override  fun onFailure(call: Call<List<ExhCounselNum>>, error: Throwable) {
                Log.d("ExhCounselNum", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<ExhCounselNum>>,
                response: Response<List<ExhCounselNum>>
            ) {
                val responseBody = response.body()!!

                var kvalautonum =  responseBody[0].counselautonum

                Log.d("ExhCounselNum" , "{$kvalautonum}")
                //  kvalautonum = "T2025-01-22-09"
                if(kvalautonum == "0"){
                    kvalautonum = "T"+keyword2+"-01"
                }else{
                    var tlength = kvalautonum.length
                    var tstr1 = kvalautonum.substring(tlength-2 , tlength).toInt()+1
                    var tstr11 = tstr1.toString()
                    if(tstr1 < 10) {
                        tstr11 = "0"+tstr1
                    }
                    kvalautonum = kvalautonum.substring(0 , tlength-2)+tstr11
                }
                binding.txtcodeNum.text = kvalautonum.toEditable()
                exhNum = kvalautonum
            }
        })
    }


    private fun getExhUpdate(service: RetrofitService, keyword1:String , keyword2:String){
        service.exhibitionUpdate(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<ExhibitionUpdate>> {
            override  fun onFailure(call: Call<List<ExhibitionUpdate>>, error: Throwable) {
                Log.d("ExhibitionList", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<ExhibitionUpdate>>,
                response: Response<List<ExhibitionUpdate>>
            ) {
                val responseBody = response.body()!!

                Log.d("exhUpdateData1" , "{$responseBody.size}")

                //val myStringBuilder = StringBuilder()
                //exhListDataArr.clear()
                //mutableexhMap.clear()
               // exhListDataArr.add("전시회 선택")
                for(exhUpdateData in responseBody) {
//                    exhListDataArr.add( exhListData1.cname)
//                    mutableexhMap.put(exhListData1.ckey , exhListData1.cname)
//                    private var exhSelCode : String = ""  // 전시회코드
//                    private var exhDate : String = ""     // 전시회상담일자
//                    private var memempmgnum : String = "" // 상담자
//                    private var comCd : String = ""      // 상담자 회사
//                    private var partnerEmpNo : String = "" // 동반자 번호
//                    private var exhNum : String = ""     // 상담일지코드번호
//                    private var exhComName : String   = ""      // 상담업체명
//                    private var exhSampleCnt : Int   = 0       // 상담업체 샘플수
//                    private var exhSangdamCnt : Int  = 0      // 상담일지장수
//                    private var exhSampleRtnYN : String = "1"   // 샘플반송여부
//                    private var vautonum : String = ""  // API를 통해 가져오 오토발번코드
                      vautonum =   exhUpdateData.uvautonum
                      exhSelCode = exhUpdateData.uexhSelCode
                      exhDate  = exhUpdateData.uexhDate
                      binding.txtcousdate.text = exhDate.toEditable()


                     //API서비스 : 전시회리스트 가져오기
                     getExhList(supplementService,"${BuildConfig.API_KEY}")

                     getExhDetail(supplementService,vautonum , "${BuildConfig.API_KEY}")

                      //동반자정보셋팅
                      getexhpartner1(supplementService, exhUpdateData.upartnerEmpNo,"${BuildConfig.API_KEY}")

                      // 상담일지코드번호
                      binding.txtcodeNum.text = exhUpdateData.uexhNum.toEditable()

                      exhNum =  exhUpdateData.uexhNum
                      binding.txtcomname.text = exhUpdateData.uexhComName.toEditable()
                      binding.txtsamplecnt.text = exhUpdateData.uexhSampleCnt.toString().toEditable()
                      binding.txtsangdamcnt.text = exhUpdateData.uexhSangdamCnt.toString().toEditable()

                      if(exhUpdateData.uexhSampleRtnYN1=="1"){
                          binding.rbrtnflag2.isChecked = true
                          exhSampleRtnYN = "N"
                      }else{
                          binding.rbrtnflag1.isChecked = true
                          exhSampleRtnYN = "Y"
                      }





                }
            }
        })
    }
    private fun getExhList(service: RetrofitService, keyword1:String){
        service.exhibitionList(keyword1).enqueue(object: retrofit2.Callback<List<ExhibitionList>> {
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

                val value1 = mutableexhMap[exhSelCode]
                binding.splistexhibition.setSelection(exhListDataArr.indexOf("${value1}"))

            }
        })
    }

    private fun getExhDelete(service: RetrofitService, keyword1:String , keyword2:Int, keyword3:String){
        service.exhibitionDelete(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<String> {
            override fun onFailure(call: Call<String>, error: Throwable) {
                Log.d("ExhibitionDetail", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                val responseBody = response.body()!!
                Toast.makeText( this@ExhibitionUpdateActivity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun getExhDetail(service: RetrofitService, keyword1:String , keyword2:String){
        service.exhibitionDetail(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<ExhibitionDetail>> {
            override  fun onFailure(call: Call<List<ExhibitionDetail>>, error: Throwable) {
                Log.d("ExhibitionDetail", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<ExhibitionDetail>>,
                response: Response<List<ExhibitionDetail>>
            ) {
                val responseBody = response.body()!!
                var kstr2 = responseBody.size
                var kflagint: Int = 0

                Log.d("ExhibitionDetail" , "{$responseBody.size}")
                var imgstr1 = ""
                //val myStringBuilder = StringBuilder()






                for(exhDetailData1 in responseBody) {

                    var prefixattr3  = ""
                    var attr5 = ""
                    var imgUrl1 = ""


                    prefixattr3 = "http://herpold.asunghmp.biz/FTP/IMAGES/CLNTCON/BCON/"
                    attr5 = exhDetailData1.filenme+"."+exhDetailData1.fileext
                    attr5  = attr5.trim()
                    var vatmnum = exhDetailData1.filenme.trim()
                    var vyear = exhDetailData1.filenme.substring(2,6).trim()
                    var vmm= exhDetailData1.filenme.substring(6,8).trim()
                    imgUrl1 = prefixattr3+vyear+"/"+vmm+"/"+attr5
                    imgUrl1 = imgUrl1.trim()

                    Log.d("designimage1" , "{$attr5}")
                    data9.add(attr5)
                    requestGet1(supplementService,"${BuildConfig.API_KEY}", attr5, imgUrl1)

                    Handler(Looper.getMainLooper()).postDelayed({

                        val imgUrl: String =
                            "http://59.10.47.222:3000/static/" + attr5

                        Glide.with(binding.photoView.context)
                            .load(imgUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(android.R.drawable.stat_notify_error)
                            .into(binding.photoView)

                        // 실행 할 코드
                    }, 1000)


                    if(kflagint < 1) {
                        kurl3 = "http://59.10.47.222:3000/static/" + attr5
                    }
                    kflagint = kflagint + 1

                    vseq =  exhDetailData1.seq.toInt()
                }

                kstr2 = data9.size
                Log.d("designimage" , "{$kstr2}")

                if(kstr2 > 1) {
                    binding.dssp.visibility = View.VISIBLE
                }

                // data9 = mutableListOf("1000382.jpg","1031154.jpg","1031155.jpg")
                //데이터와 스피터를 연결 시켜줄 adapter를 만들어 준다.
                //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
                var adapter9 = ArrayAdapter<String>(this@ExhibitionUpdateActivity, android.R.layout.simple_spinner_item,data9)
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
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .error(android.R.drawable.stat_notify_error)
                            .into(binding.photoView)


                        // 실행 할 코드
                    }, 500)


                }


                if (kflagint==0){
                    binding.photoView.visibility = View.GONE
                    binding.btnexpan1.visibility = View.GONE
                }
                binding.btnexpan1.setOnClickListener {

                    Log.d("btnexpan1", "$kurl3")

                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse("$kurl3"))
                    startActivity(intent)
                    //myWebView.loadUrl("${kurl1}")
                }


//                var imgstr2 = imgstr1.substring(0,imgstr1.length-1).trim()
//
//                Log.d("timg_imgstr2" , "${imgstr2}")
//                val recyclerView: RecyclerView = findViewById(R.id.recyclerView1)
//                recyclerView.layoutManager = GridLayoutManager(this@ExhibitionUpdateActivity, 2)  // 2열 그리드 레이아웃
//
//                recyclerView.adapter = ImageAdapter1(getImageUrls(imgstr2))

            }
        })
    }


    fun createDialog(pparnercount: MutableList<String>){

        val dataList = pparnercount



        val adapter = ArrayAdapter<String>(
            this, android.R.layout.select_dialog_singlechoice ,dataList
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("동행자 선택")
        // builder에 어뎁터 설정
        // 두 번째 매개변수에는 사용자가 선택한 항목의 순서값이 들어온다.
        builder.setAdapter(adapter){ dialogInterface: DialogInterface, i: Int ->
            // setTextView.text = "선택한 항목 : ${dataList[i]}"
            binding.txtpartTotal.text = dataList[i]
            binding.txtpartUser.text = "".toEditable()

            val schstr =  dataList[i]
            val searchkey1 = "("
            val searchkey2 = ")"

            val indexOf1 = schstr.indexOf(searchkey1)+1
            val indexOf2 = schstr.indexOf(searchkey2)

            partnerEmpNo = schstr.substring(indexOf1, indexOf2)
            // Log.d("testtest" , "${partnerEmpNo}")
            keyBordHide()
        }


        builder.setNegativeButton("취소",null)
        builder.show()

    }

    fun createDialog1(clientcount: MutableList<ClientCount>){

        val dataList =  mutableListOf<String>()

        for(i: Int in 0..clientcount.size-1){
            dataList.add(clientcount[i].clientBizNameK)
            // Log.d("CSearch", clientcount[i].clientBizNameK.toString())
        }
        Log.d("CSearch", "1")
        val adapter = ArrayAdapter<String>(
            this, android.R.layout.select_dialog_singlechoice ,dataList
        )

        Log.d("CSearch", "2")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("거래처 선택")
        // builder에 어뎁터 설정
        // 두 번째 매개변수에는 사용자가 선택한 항목의 순서값이 들어온다.
        builder.setAdapter(adapter){ dialogInterface: DialogInterface, i: Int ->
            // setTextView.text = "선택한 항목 : ${dataList[i]}"

            Log.d("CSearch", "3")
            ClientnameK =  dataList[i]
            ClientcodeP = clientcount[i].clientBizNameK.toString()

            //  clientGetData1()
            binding.txtcomname.text = ClientcodeP.toEditable()


        }

        Log.d("CSearch", "4")
        builder.setNegativeButton("취소",null)
        builder.show()
        //binding.svClient.clearFocus()

    }



    private fun getClientSchCountList(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.comView11(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataClientSchDetail1>> {
            override  fun onFailure(call: Call<List<DataClientSchDetail1>>, error: Throwable) {
                Log.d("comView1", "실패원인: {$error}")
            }
            override fun onResponse(
                call: Call<List<DataClientSchDetail1>>,
                response: Response<List<DataClientSchDetail1>>
            ) {
                // BuyerCd = keyword2
                Log.d("CSearch", "거래처명  API 성공 ")
                val responseBody = response.body()!!
                clientcount = mutableListOf<ClientCount>()
                clientDialog1 = mutableListOf<String>()
                for(clientcnt1 in responseBody) {
                    clientcount.add( ClientCount(clientcnt1.clientNoP , clientcnt1.clientBizNameK))
                    clientDialog1.add(clientcnt1.clientBizNameK)
                }
                Log.d("CSearch", clientcount.size.toString())
                if(clientcount.size > 1) { //거래처가 1개이상  존재할 경우
                    createDialog1(clientcount)
                }else if(clientcount.size == 1) { // 거래처가 1개일 경우
                    ClientcodeP =  clientcount[0].clientBizNameK
                    if(ClientcodeP.isNotEmpty()) {
                        //  getClientSchList1(supplementService, comCd, ClientcodeP, "${BuildConfig.API_KEY}")
                        binding.txtcomname.text = ClientcodeP.toEditable()
                    }
                }else { //거래처번호가 존재 하지 않을 경우

                    noitemDisplay()

                }

                ClientcodeP = binding.txtschcomname.toString()
                Log.d("ItevSearch" , "거래처번호 + ${ClientcodeP}")

            }
        })
    }


    private fun getexhpartner1(service: RetrofitService, keyword1:String, keyword2:String){
        service.exhpartner1(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<DataExhPartner1>> {
            override  fun onFailure(call: Call<List<DataExhPartner1>>, error: Throwable) {
                Log.d("DataExhPartner333", "실패원인: {$error}")
            }
            override fun onResponse(
                call: Call<List<DataExhPartner1>>,
                response: Response<List<DataExhPartner1>>
            ) {
                // BuyerCd = keyword2
                Log.d("DataExhPartner", "DataExhPartner API 성공 ")
                val responseBody = response.body()!!
                //    partnercount partnerDialog1
                dataexhpartnercount1 = mutableListOf<DataExhPartnerCount1>()

                for(partnercnt1 in responseBody) {
                    dataexhpartnercount1.add( DataExhPartnerCount1(partnercnt1.nme , partnercnt1.hnme))
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
                    binding.txtpartTotal.text = ktitle

                    partnerEmpNo =  partnercnt1.nme.trim()
                }





            }
        })
    }



    private fun getexhpartner(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
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
                    createDialog(partnerDialog1)
                }else if(dataexhpartnercount.size == 1) { // 거래처가 1개일 경우
                    createDialog(partnerDialog1)
                }else {

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

    private fun keyBordHide() {
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    }

    private fun noitemDisplay(){
        // binding.rvHerpClientlist1.visibility = View.GONE
        // binding.rvHerpClientlist2.visibility = View.GONE
    }


    fun uploadFileToFtp(file: File, ftpServer: String, ftpUsername: String, ftpPassword: String, ftpDirectory: String) {
        val ftpClient = FTPClient()
        ftpClient.connect(ftpServer)
        ftpClient.login(ftpUsername, ftpPassword)
        ftpClient.enterLocalPassiveMode()
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
        ftpClient.changeWorkingDirectory(ftpDirectory)

        val inputStream = FileInputStream(file)
        val fileName = file.name
        ftpClient.storeFile(fileName, inputStream)
        inputStream.close()

        ftpClient.logout()
        ftpClient.disconnect()
    }




    override fun onDestroy() {
        // onDestroy 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        mbinding = null
        super.onDestroy()
    }


    private fun getImageUrls(str1:String): List<String> {
        return listOf(str1.trim())
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


    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("확인")
        builder.setMessage("삭제하시겠습니까?")
        builder.setPositiveButton("예") { dialog, _ ->
            // 예 버튼 클릭 시 실행할 작업
            val dialog = LoadingDialog(this)

            dialog.show()
            getExhDelete(supplementService,vautonum , vseq , "${BuildConfig.API_KEY}")
        }
        builder.setNegativeButton("아니요") { dialog, _ ->
            // 아니요 버튼 클릭 시 실행할 작업
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    fun Spinner.selected(action: (position:Int) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                action(position)
            }
        }
    }


    fun clearCache(context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
//            Glide.get(context).clearDiskCache() // 디스크 캐시 삭제
            Glide.get(context).clearMemory() // 메모리 캐시 삭제
        }
    }
}