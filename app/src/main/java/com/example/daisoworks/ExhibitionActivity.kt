package com.example.daisoworks

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.adapter.ExpandableAdapterMyList
import com.example.daisoworks.data.ExhibitionMyList
import com.example.daisoworks.databinding.ActivityExhibitionBinding
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


    //spinner의 값을 가지고 위의 배열에서 바이어시디코드를 저장함.
    private   var exhListCd : String = ""

    private var exhSdate : String = ""     // 전시회상담조회시작일자
    private var exhEdate : String = ""     // 전시회상담조회종료일자
    private var memempmgnum : String = "" // 상담자
    private var vcompanycode : String = ""
    private var id1 : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // setContentView(R.layout.activity_exhibition)

        prefs = PreferenceUtil(applicationContext)
        // 1. 바인딩 초기화
        // 자동 생성된 뷰 바인딩 클래스에서의 inflate라는 메서드를 활용해서
        // 액티비티에서 사용할 바인딩 클래스의 인스턴스 생성
        mbinding = ActivityExhibitionBinding.inflate(layoutInflater)

        // getRoot 메서드로 레이아웃 내부의 최상위 위치 뷰의
        // 인스턴스를 활용하여 생성된 뷰를 액티비티에 표시 합니다.
        setContentView(binding.root)
        //기본 Actionbar 제목 변경
        getSupportActionBar()?.setTitle("전시회 상담관리")
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)

        val now: LocalDateTime = LocalDateTime.now()
        val vdateFormat = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val cal = Calendar.getInstance()

        var sevendays = (now.plusDays(-6)).format(formatter).toString()

       binding.txtcousdate.setText(sevendays.format(formatter).toString())
       binding.txtcouedate.setText(now.format(formatter).toString())

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
    }

//in  : memempmgnum , exhDate , apikey
//out : tclntConNo, exhNum , exhComName ,exhDate

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