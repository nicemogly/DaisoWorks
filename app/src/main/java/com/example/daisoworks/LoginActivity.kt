package com.example.daisoworks

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.daisoworks.data.ChkVer
import com.example.daisoworks.data.HerpMemberInfo
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class LoginActivity : AppCompatActivity() {

    lateinit var main: ConstraintLayout
    lateinit var btnLogin: Button
    lateinit var editTextId: EditText
    lateinit var editTextPassword: EditText
    lateinit var autoLogin: SwitchCompat
    private lateinit var retrofit : Retrofit
    private lateinit var retrofit1 : Retrofit
    private lateinit var retrofit2 : Retrofit
    private lateinit var retrofit3 : Retrofit
    private  lateinit var supplementService : RetrofitService
    private  lateinit var supplementService1 : RetrofitService1
    private  lateinit var supplementService2 : RetrofitService2
    private  lateinit var supplementService3 : RetrofitService3
    private var SversionName : String  = ""
    private var SversionMsg : String  = ""
    private var versionName : String  = ""
    private var versionFkag : String  = "N"

    private var Deptgbn : String  = ""
    private var Deptnme : String  = ""
    private var Deptcde : String  = ""

    //내부저장소 변수 설정
    companion object{
        lateinit var prefs: PreferenceUtil
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect: Rect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Dark Mode 사용 안함.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //내부저장소 초기화
        prefs = PreferenceUtil(applicationContext)

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비는 내부에 둠)
        //HR
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        ///HR 임원여부
        retrofit2 = RetrofitClient2.getInstance2()
        supplementService2 = retrofit2.create(RetrofitService2::class.java)


        //APPVERSION
        retrofit1 = RetrofitClient1.getInstance1()
        supplementService1 = retrofit1.create(RetrofitService1::class.java)

        //Herp Member INFO
        retrofit3 = RetrofitClient3.getInstance3()
        supplementService3 = retrofit3.create(RetrofitService3::class.java)


        super.onCreate(savedInstanceState)

        //StatusBar와 NavigationBar까지 화면을 확장
        enableEdgeToEdge()

        //fragment Binding
        setContentView(R.layout.activity_login)

        //로그인전 APP Version Check
        versionName = BuildConfig.VERSION_NAME
        getVersionName(supplementService1,"${BuildConfig.API_KEY}")
       // var  loginFlag: String? = response.body()?.loginList?.get(0)?.VALUE?.toString()
       // if(versionName.equals())

        //내부저장소 자동로그인 여부 가져오기 , 기본값은 0
        val autoLoginFlagS = prefs.getString("autoLoginFlagS","0")

        //자동로그인 분기처리
        if(autoLoginFlagS=="0") { //자동로그인이 아니면
            findViewById<SwitchCompat?>(R.id.category_toggle_iv).isChecked = false
        }else{  // 자동로그인이면
            findViewById<SwitchCompat?>(R.id.category_toggle_iv).isChecked = true

            //디자이스별 내부 저장소에서 id,pw 값을 가져옴
            var id1 = prefs.getString("id", "none")
            var pw1 = prefs.getString("pw", "none")


            Toast.makeText(this@LoginActivity, "자동저장된 값으로 로그인중입니다.", Toast.LENGTH_SHORT).show()
            //RETRO API 호출(파라미터값 설정)
          //  var id2 = "AH1304050"
        //    Log.d("로그인" , "1")
            getMemberInfo(supplementService3, "${BuildConfig.API_KEY}"  ,"$id1") //Herp Member INFO 최백호
          //  Log.d("로그인" , "2")
            getLoginList2(supplementService2, "IN_INPUT","OUT_RESULT","$id1") //임원여부
            //Log.d("로그인" , "3")
            getLoginList(supplementService, "IN_INPUT","OUT_RESULT","$id1","$pw1")  //계정1차확인
           Log.d("로그인" , "4")
//            Log.d("로그인id" , "$id1")
//            Log.d("로그인pw" , "$pw1")
         }

        //화면 Object 변수 설정
        main = findViewById(R.id.main)
        btnLogin = findViewById(R.id.btnLogin)
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)



        // 로그인 버튼 클릭(오토로그인 아님)
        btnLogin!!.setOnClickListener {
            val user = editTextId!!.text.toString()
            val pass = editTextPassword!!.text.toString()

            main.clearFocus()

            // 빈칸 제출시 Toast처리
            if (user == "" || pass == "") {
                Toast.makeText(this@LoginActivity, "사번과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                var id = user.trim()//trim : 문자열 공백제거
                var pw = pass.trim()
                //RETRO API 호출(파라미터값 설정)

               // id = "AS1410020"
            //    var id1 = "AH1304050"
                getMemberInfo(supplementService3, "${BuildConfig.API_KEY}"  ,"$id") //Herp Member INFO 최백호
                getLoginList2(supplementService2, "IN_INPUT","OUT_RESULT","$id") //임원여부
                getLoginList(supplementService, "IN_INPUT","OUT_RESULT","$id","$pw")  //계정1차확인

            }
        }
    }

    // API RETROFIT2 호출 함수 구현( HR에서 제공해준 API주소에 인자값 셋팅 , GET방식, 파라미터1, 2 는 HR에서 제공해준값 ,파라미터 3,4 는 앱에서 던짐.)
    // Callback시 GSON 형식에 맞게끔 데이터 클래스 담아야 함.여기서 에러주의, Json 형식 맞아야하고 GSON 변환도 맞아야함.
    private fun getLoginList(service: RetrofitService, keyword1:String, keyword2:String,keyword3:String,keyword4:String){
        service.userLogin(keyword1,keyword2,keyword3,keyword4).enqueue(object: retrofit2.Callback<LoginList> {
            override  fun onFailure(call: Call<LoginList>, error: Throwable) {
                Log.d("로그인", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<LoginList>,
                response: Response<LoginList>
            ) {
                Log.d("로그인" , "5")
                // RetroFit2 API 로그인 결과값중 VALUE값 셋팅
                var  loginFlag: String? = response.body()?.loginList?.get(0)?.VALUE?.toString()

                //HR에서 상태코드로 API결과값을 리턴해주지 않아 200,300,500 등 보통 이렇게 처리하는데 정상적인 경우는 반환값이 VALUE가 존재하는데 , 사번자리수등 Validation 처리에 걸리경우
                //리턴되는 값이 완전히 달라져서 VALUE값이  NULL로 들어옴. NULL값 분기처리가 필요함.


                if(loginFlag != null ) {

                    val aaaa = prefs.getString("excutive","0")

                    Log.d("excutive-session", "$aaaa")

                    when (loginFlag) {
                        "T" -> { //사번,비번 일치하면
                           //  내부저장소에 id,pw 등록
                            prefs.setString("id","${keyword3}")
                            prefs.setString("pw","${keyword4}")

                            // 내부저장소에 회사등록
                            var LoginCompany : String = ""
                            var LoginCompanyCode : String = ""

                            var PreLoginCompany = keyword3.substring(0,2).toUpperCase()
                            if(PreLoginCompany=="AD") {
                                LoginCompany="아성다이소"
                                LoginCompanyCode="00000"
                            }else if(PreLoginCompany=="AH") {
                                LoginCompany="아성에이치엠피"
                                LoginCompanyCode="10000"

                            }else if(PreLoginCompany=="AS") {
                                LoginCompany = "아성"
                                LoginCompanyCode="00001"
                            }

                            if(keyword3=="AD2201016" || keyword3=="AD2201004"){
                                prefs.setString("company","아성에이치엠피")
                                prefs.setString("companycode","10000")

                                LoginCompany="아성에이치엠피"
                                LoginCompanyCode="10000"

                            }else {
                                prefs.setString("company","${LoginCompany}")
                                prefs.setString("companycode","${LoginCompanyCode}")
                            }



                            // autoLoginFlag1 초기화
                            var autoLoginFlag1: String = "0"
                            //현재 자동로그인 체크여부 확인
                            val autoLoginFlag: Boolean =
                                findViewById<SwitchCompat?>(R.id.category_toggle_iv).isChecked
                            //자동로그인 분기 처리
                            if (autoLoginFlag) {
                                autoLoginFlag1 = "1"
                            } else {
                                autoLoginFlag1 = "0"
                            }

                            //내부저장소 자동로그인 여부 저장
                            prefs.setString("autoLoginFlagS", "${autoLoginFlag1}")


                            Log.d("HERP-Deptgbn", "$Deptgbn")
                            Log.d("HERP-Deptnme", "$Deptnme")
                            Log.d("HERP-Deptcde", "$Deptcde")



                            //  val intent = Intent(this, MainActivity::class.java)
                            // 함수 안이라 Intent  안먹혀서 별도 함수로 보냄
                            // 2단계 생체인증을 위해 보냄.
                            onMove()
                        }

                        "F" -> {  //사번,비번 불일치하면
                            Toast.makeText(
                                this@LoginActivity,
                                "로그인실패: 사번 또는 비밀번호가 올바르지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        "" -> { //예외의 경우
                            Toast.makeText(
                                this@LoginActivity,
                                "로그인실패: 시스템 관리자에게 문의바랍니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Log.d("로그인", "아이디비번 1차 성공")
                }else{
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인실패: 사번 또는 비밀번호가 올바르지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }

    //임원여부 가져오기
    private fun getLoginList2(service: RetrofitService2, keyword1:String, keyword2:String,keyword3:String){
        service.userLogin2(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<LoginList2> {
            override  fun onFailure(call: Call<LoginList2>, error: Throwable) {
                Log.d("임원여부", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<LoginList2>,
                response: Response<LoginList2>
            ) {

                // RetroFit2 API 로그인 결과값중 VALUE값 셋팅
                var  login2Flag: String? = response.body()?.loginList2?.get(0)?.VALUE?.toString()


                //리턴되는 값이 완전히 달라져서 VALUE값이  NULL로 들어옴. NULL값 분기처리가 필요함.
                if(login2Flag != null ) {
                    Log.d("excutive", "{$login2Flag}")
                    when (login2Flag) {
                        "T" -> { //임원이면
                            //  내부저장소에 임원여부 등록
                            prefs.setString("excutive","T")
                        }

                        "F" -> {
                            prefs.setString("excutive","F")
                        }

                        "" -> { //예외의 경우
                            Toast.makeText(
                                this@LoginActivity,
                                "로그인실패: 시스템 관리자에게 문의바랍니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Log.d("로그인", "임원여부 성공")
                }else{
                    Toast.makeText(
                        this@LoginActivity,
                        "임원여부취득실패: 임원정보유무가  올바르지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }

    private fun getVersionName(service1: LoginActivity.RetrofitService1, keyword1:String){
           service1.checkVersion(keyword1).enqueue(object: retrofit2.Callback<List<ChkVer>> {
            override  fun onFailure(call: Call<List<ChkVer>>, error: Throwable) {
                Log.d("versionCheck", "versionCheck Failed: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<ChkVer>>,
                response: Response<List<ChkVer>>
            ) {

                val responseBody = response.body()!!
                //val myStringBuilder = StringBuilder()

                SversionName = responseBody[0].versionName
                SversionMsg = responseBody[0].versionMsg
                if(versionName.equals(SversionName)){
                    versionFkag = "Y"
                }else{
                    versionFkag = "N"
                    createDialog(SversionMsg)

                }

            }
        })
    }

    private fun getMemberInfo(service3: LoginActivity.RetrofitService3, keyword1:String , keyword2: String){
        service3.herpMemberInfo(keyword1, keyword2).enqueue(object: retrofit2.Callback<List<HerpMemberInfo>> {
            override  fun onFailure(call: Call<List<HerpMemberInfo>>, error: Throwable) {
                Log.d("HerpMemberInfo", "HerpMemberInfo Failed: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<HerpMemberInfo>>,
                response: Response<List<HerpMemberInfo>>
            ) {

                val responseBody = response.body()!!
                //val myStringBuilder = StringBuilder()

                if (responseBody.isNotEmpty() || responseBody.isNullOrEmpty()) {

                    Deptgbn = ""
                    Deptnme = ""
                    Deptcde = ""

                }else{
                    Deptgbn = responseBody[0].deptgbn
                    Deptnme = responseBody[0].deptnme
                    Deptcde = responseBody[0].deptcde

                }

                prefs.setString("memdeptgbn","$Deptgbn")
                prefs.setString("memdeptnme","$Deptnme")
                prefs.setString("memdeptcde","$Deptcde")

            }
        })
    }


    fun onMove(){
        val intent = Intent(this, Login2Activity::class.java)
        startActivity(intent)
    }

    //Retrofit Object 설정
    object RetrofitClient {
        private var instance: Retrofit? = null
        private val gson = GsonBuilder().setLenient().create()

        //BASEURL 끝에 / 빠지면 에러 남.
        private const val BASE_URL =  "https://hr.asungcorp.com/cm/service/BRS_CM_RetrieveReturnVal/"


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

    object RetrofitClient2 {
        private var instance2: Retrofit? = null
        private val gson2 = GsonBuilder().setLenient().create()

        //BASEURL 끝에 / 빠지면 에러 남.
        private const val BASE_URL2 =  "https://hr.asungcorp.com/cm/service/BRS_CM_RetrieveEmpTypeReturnVal/"

        //Retrofit 객체생성
        fun getInstance2(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

            //client 없으면 GSON , JSON형태의 데이터 클래스 생성시 에러가 나는것 같음.
            //Interceptor 해서 뭔가 오류 수정작업하는것 같음.
            val client: OkHttpClient =
                OkHttpClient.Builder().addInterceptor(interceptor).build()
            if (instance2 == null) {
                instance2 = Retrofit.Builder()
                    .baseUrl(BASE_URL2)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson2))
                    .build()
            }
            return instance2!!
        }

    }



    object RetrofitClient1 {


        private var instance1: Retrofit? = null
        private val gson1 = GsonBuilder().setLenient().create()

        //BASEURL 끝에 / 빠지면 에러 남.

        private const val BASE_URL1 = "http://59.10.47.222:3000/"

        //Retrofit 객체생성

        fun getInstance1(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

            //client 없으면 GSON , JSON형태의 데이터 클래스 생성시 에러가 나는것 같음.
            //Interceptor 해서 뭔가 오류 수정작업하는것 같음.
            val client: OkHttpClient =
                OkHttpClient.Builder().addInterceptor(interceptor).build()
            if (instance1 == null) {
                instance1 = Retrofit.Builder()
                    .baseUrl(BASE_URL1)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson1))
                    .build()
            }
            return instance1!!
        }


    }




    object RetrofitClient3 {


        private var instance3: Retrofit? = null
        private val gson3 = GsonBuilder().setLenient().create()

        //BASEURL 끝에 / 빠지면 에러 남.

        private const val BASE_URL3 = "http://59.10.47.222:3000/"

        //Retrofit 객체생성

        fun getInstance3(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

            //client 없으면 GSON , JSON형태의 데이터 클래스 생성시 에러가 나는것 같음.
            //Interceptor 해서 뭔가 오류 수정작업하는것 같음.
            val client: OkHttpClient =
                OkHttpClient.Builder().addInterceptor(interceptor).build()
            if (instance3 == null) {
                instance3 = Retrofit.Builder()
                    .baseUrl(BASE_URL3)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson3))
                    .build()
            }
            return instance3!!
        }


    }



    //Retrofit Service : 전송방식(GET,POST....) , Parameter 세팅가능
    interface RetrofitService {
        @GET("ajax.ncd")
        fun userLogin(
            @Query("baRq") param1: String,
            @Query("baRs") param2: String,
            @Query("IN_INPUT.USER_NM") param3: String,
            @Query("IN_INPUT.VALUE") param4: String
        ): Call<LoginList>

    }

    interface RetrofitService2 {
        @GET("ajax.ncd")
        fun userLogin2(
            @Query("baRq") param1: String,
            @Query("baRs") param2: String,
            @Query("IN_INPUT.USER_NM") param3: String
        ): Call<LoginList2>

    }



    interface RetrofitService1 {
        @GET("checkversion")
        fun checkVersion(
            @Query("apikey") param1: String
        ): Call<List<ChkVer>>

    }


    interface RetrofitService3 {
        @GET("memuser")
        fun herpMemberInfo(
            @Query("apikey") param1: String,
            @Query("mUserId") param2: String
        ): Call<List<HerpMemberInfo>>

    }


    // 계정여부 Data Class(결과값, CALL BACK시 API통신 결과값을 담는다. {"OUT_RESULT":[{"VALUE":"T"}]}
    data class LoginList(
        @SerializedName("OUT_RESULT")
        val loginList : List<Login>
    )
    data class Login(
        var VALUE: String
    )

    // 임원여부 Data Class(결과값, CALL BACK시 API통신 결과값을 담는다. {"OUT_RESULT":[{"VALUE":"T"}]}
    data class LoginList2(
        @SerializedName("OUT_RESULT")
        val loginList2 : List<Login2>
    )
    data class Login2(
        var VALUE: String
    )



    fun createDialog( str1: String){
        var versionTitle = resources.getString(R.string.versioninfo)
        var builder = AlertDialog.Builder(this)
        builder.setTitle(versionTitle.toString())
        builder.setMessage(str1)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setNegativeButton("확인", listener)
        builder.show()

    }

    var listener = DialogInterface.OnClickListener { p0, p1 ->
        System.exit(0)
    }

}


