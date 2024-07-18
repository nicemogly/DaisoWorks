package com.example.daisoworks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class LoginActivity : AppCompatActivity() {

    lateinit var btnLogin: Button
    lateinit var editTextId: EditText
    lateinit var editTextPassword: EditText
    lateinit var autoLogin: SwitchCompat


    companion object{
        lateinit var prefs: PreferenceUtil
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
       prefs = PreferenceUtil(applicationContext)

        Log.d("testtest_logintop","22222")

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        val autoLoginFlagS = prefs.getString("autoLoginFlagS","0")


        Log.d("testtest_logintop","$autoLoginFlagS")


        if(autoLoginFlagS=="0") {
            findViewById<SwitchCompat?>(R.id.category_toggle_iv).isChecked = false

        }else{
            findViewById<SwitchCompat?>(R.id.category_toggle_iv).isChecked = true
             val intent = Intent(this, MainActivity::class.java)
             startActivity(intent)
        }




        btnLogin = findViewById(R.id.btnLogin)
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)

        // 로그인 버튼 클릭
        btnLogin!!.setOnClickListener {
            val user = editTextId!!.text.toString()
            val pass = editTextPassword!!.text.toString()

            // 빈칸 제출시 Toast
            if (user == "" || pass == "") {
                Toast.makeText(this@LoginActivity, "사번과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                //val checkUserpass = DB!!.checkUserpass(user, pass)
                val checkUserpass = true
                // id 와 password 일치시
                if (checkUserpass == true) {
                    Toast.makeText(this@LoginActivity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                    var autoLoginFlag1 : String = "0"
                    val autoLoginFlag:Boolean = findViewById<SwitchCompat?>(R.id.category_toggle_iv).isChecked

                    //Log.d("testtest","$autoLoginFlag")
                    if(autoLoginFlag){
                        autoLoginFlag1 = "1"
                    }else {
                        autoLoginFlag1 = "0"
                    }
                    prefs.setString("autoLoginFlagS","${autoLoginFlag1}")


                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this@LoginActivity, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }








    }



}

