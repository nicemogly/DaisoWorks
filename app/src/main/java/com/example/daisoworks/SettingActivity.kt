package com.example.daisoworks

import android.R.attr.button
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController


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
        tv1?.setText("현재버전 " + versionName)

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



        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
    }

    //onSupportNavigateUp() 메소드를 오버라이딩해서, AppBar에 생성되는 뒤로가기 버튼을 눌렀을 때 뒤로 이동시킨다.
    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_setting)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/


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