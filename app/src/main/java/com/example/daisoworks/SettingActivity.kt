package com.example.daisoworks

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat


class SettingActivity : AppCompatActivity() {

    //변수 선언
    private lateinit var loginToggle: SwitchCompat
    var tv1: TextView? = null


    //내부저장소 변수 설정
    companion object{
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


        //버전 확인
        var versionName = BuildConfig.VERSION_NAME
        tv1 = findViewById(R.id.txtVersionN)
        tv1?.setText(versionName)

        //내부저장소 자동로그인 여부 가져오기 , 기본값은 0
        val autoLoginFlagS = prefs.getString("autoLoginFlagS","0")

        //자동로그인 분기처리
        if(autoLoginFlagS=="0") { //자동로그인이 아니면
            findViewById<SwitchCompat?>(R.id.login_toggle).isChecked = false
        }else {  // 자동로그인이면
            findViewById<SwitchCompat?>(R.id.login_toggle).isChecked = true
        }

        //토글 선택시
        loginToggle = findViewById(R.id.login_toggle)
        loginToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) { // on
                prefs.setString("autoLoginFlagS", "1")
                Log.d("babo" , "1")
            } else {    // off
                prefs.setString("autoLoginFlagS", "0")
                Log.d("babo" , "0")
            }
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