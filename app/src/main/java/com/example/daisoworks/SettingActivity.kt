package com.example.daisoworks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat


class SettingActivity : AppCompatActivity() {

    //변수 선언
    private lateinit var loginToggle: SwitchCompat
    var tv1: TextView? = null


    var imm : InputMethodManager? = null
    //내부저장소 변수 설정
    companion object {
        lateinit var prefs: PreferenceUtil
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        // 내부저장소
        prefs = PreferenceUtil(applicationContext)

        // 레이아웃 표시
        setContentView(R.layout.activity_setting)

        //기본 Actionbar 제목 변경
        getSupportActionBar()?.setTitle("설정")
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)
        Log.d("babo", "0")

        //버전 확인
        var versionName = BuildConfig.VERSION_NAME
        tv1 = findViewById(R.id.txtVersionN)
        tv1?.setText(versionName)

        //내부저장소 자동로그인 여부 가져오기 , 기본값은 0
        val autoLoginFlagS = prefs.getString("autoLoginFlagS", "0")
        val lcomcode = prefs.getString("companycode", "0")
        val lexcutive = prefs.getString("excutive", "0")
        val lid = prefs.getString("id", "0")


        //자동로그인 분기처리
        if (autoLoginFlagS == "0") { //자동로그인이 아니면
            findViewById<SwitchCompat?>(R.id.login_toggle).isChecked = false
        } else {  // 자동로그인이면
            findViewById<SwitchCompat?>(R.id.login_toggle).isChecked = true
        }

        //토글 선택시
        loginToggle = findViewById(R.id.login_toggle)
        loginToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) { // on
                prefs.setString("autoLoginFlagS", "1")

            } else {    // off
                prefs.setString("autoLoginFlagS", "0")

            }
        }

        imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        var id1 = prefs.getString("id", "none")


        var txt11: TextView = findViewById(R.id.txt11)
        var txt12: TextView = findViewById(R.id.txt12)
        var txt22: TextView = findViewById(R.id.txt22)
        var txt33: TextView = findViewById(R.id.txt33)
        var txt333: TextView = findViewById(R.id.txt333)

        var rc: RadioGroup = findViewById(R.id.radio_company)
        var re: RadioGroup = findViewById(R.id.radio_expg)
        val btnSetSave: Button = findViewById(R.id.btnSetSave)
        val setuserid: TextView = findViewById(R.id.setuserid)


        var cl1: View = findViewById(R.id.cl1)


        if(id1=="AD2201016" || id1=="AD2201005" || id1=="AD2201004" ){



            val radio_company: RadioGroup = findViewById(R.id.radio_company)

            if (lcomcode == "10005") {
                radio_company.check(R.id.radio_comtype1)
            } else if (lcomcode == "10000"){
                radio_company.check(R.id.radio_comtype2)
            }else if (lcomcode == "00001"){
                radio_company.check(R.id.radio_comtype3)
            }

            radio_company.setOnCheckedChangeListener { radioGroup, i ->
                when(i){
                    R.id.radio_comtype1 ->  prefs.setString("companycode", "10005")
                    R.id.radio_comtype2 ->  prefs.setString("companycode", "10000")
                    R.id.radio_comtype3 ->  prefs.setString("companycode", "00001")
                }
                Toast.makeText( this, "저장하였습니다.", Toast.LENGTH_SHORT).show()
            }

            val radio_expg: RadioGroup = findViewById(R.id.radio_expg)
            if (lexcutive == "T") {
                radio_expg.check(R.id.radio_exp1)
            }else{
                radio_expg.check(R.id.radio_exp2)
            }

            radio_expg.setOnCheckedChangeListener { radioGroup, i ->
                when(i){
                    R.id.radio_exp1 ->  prefs.setString("excutive", "T")
                    R.id.radio_exp2 ->  prefs.setString("excutive", "F")
                }
                Toast.makeText( this, "저장하였습니다.", Toast.LENGTH_SHORT).show()
            }

            //   val btnSetSave: Button = findViewById(R.id.btnSetSave)
            //    val setuserid: TextView = findViewById(R.id.setuserid)

            setuserid.text = lid

            btnSetSave.setOnClickListener(){
                prefs.setString("id", setuserid.text.toString())

               var ktmpuserid =  setuserid.text.toString()

                //김교령  HS1106470  - HMP
                //최군    HS1106240  - (주)아성
                if(ktmpuserid=="HS1106470"){
                    prefs.setString("company","아성에이치엠피")
                    prefs.setString("companycode","10000")
                    prefs.setString("memempmgnum","430")
                }else if(ktmpuserid=="HS1106240") {
                    prefs.setString("company","아성")
                    prefs.setString("companycode","00001")
                    prefs.setString("memempmgnum","452")
                }

                hideKeyboard(it)
                Toast.makeText( this, "저장하였습니다.", Toast.LENGTH_SHORT).show()
            }

        }else{
            Log.d("babo", "1")
            cl1.visibility = View.INVISIBLE
            txt11.visibility = View.INVISIBLE
            txt333.visibility = View.INVISIBLE
//            txt12.visibility = View.INVISIBLE
//            txt22.visibility = View.INVISIBLE
//            txt33.visibility = View.INVISIBLE
//            rc.visibility = View.INVISIBLE
//            re.visibility = View.INVISIBLE
//            btnSetSave.visibility = View.INVISIBLE
//            setuserid.visibility = View.INVISIBLE
        }


//        var rc = findViewById(R.id.radio_company)
//        var re = findViewById(R.id.radio_expg)
//        val btnSetSave: Button = findViewById(R.id.btnSetSave)
//        val setuserid: TextView = findViewById(R.id.setuserid)





    }
    fun hideKeyboard(v: View) {
        if(v != null) {
            imm?.hideSoftInputFromWindow(v.windowToken, 0 )
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
}