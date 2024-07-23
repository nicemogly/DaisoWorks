package com.example.daisoworks

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.daisoworks.databinding.ActivityLogin2Binding
import java.util.concurrent.Executor

class  Login2Activity : AppCompatActivity()  {

    lateinit var binding: ActivityLogin2Binding

    private lateinit var  executor: Executor
    private lateinit var  biometricPrompt: BiometricPrompt
    private lateinit var  prompInfo : BiometricPrompt.PromptInfo
    private lateinit var  prompInfo1 : BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding=ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgFingure.setOnClickListener{
            checkDeviceHasBiometric()
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt=BiometricPrompt(this,executor,object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    //TODO - 생체 인식이 안될 경우 비밀번호 입력할 수 있도록 기능 추가
                    //biometricPrompt.authenticate(prompInfo1)
                    btnLogin1Change()
                }

               Toast.makeText(this@Login2Activity,"인증오류 : $errString", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@Login2Activity,"인증 성공", Toast.LENGTH_LONG).show()

                letsgo()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@Login2Activity,"인증 실패", Toast.LENGTH_SHORT).show()
            }
        })

        prompInfo=BiometricPrompt.PromptInfo.Builder()
            .setTitle("ASUNG Authentication")
            .setSubtitle("Two_Fact Authentication")
           // .setAllowedAuthenticators( BIOMETRIC_STRONG)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        prompInfo1=BiometricPrompt.PromptInfo.Builder()
            .setTitle("ASUNG Authentication")
            .setSubtitle("Two_Fact Authentication")
            // .setNegativeButtonText("Pin Login")
            .setAllowedAuthenticators( DEVICE_CREDENTIAL)
            .build()



        binding.btnLogin.setOnClickListener{
            biometricPrompt.authenticate(prompInfo)
        }


        binding.btnLogin1.setOnClickListener{
            biometricPrompt.authenticate(prompInfo1)
        }


    }

    fun letsgo(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun btnLogin1Change(){
        binding.btnLogin1.isVisible = true
        binding.btnLogin1.isEnabled = true
        binding.btnLogin1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
    }

    fun letsgo2(){
      //  val intent = Intent(this, Login3Activity::class.java)
      //  startActivity(intent)
    }
    fun checkDeviceHasBiometric(){
        val biometricManager = BiometricManager.from(this)
        when(biometricManager.canAuthenticate(BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)){
            BiometricManager.BIOMETRIC_SUCCESS -> {
              //  Log.d("My_APP_TAG","지문인식을 지원하는 기기입니다. \n 로그인 버튼을 누르세요")
                binding.tvMsg.text = "지문인식을 지원하는 기기입니다. \n 로그인 버튼을 누르세요"
                binding.btnLogin.isEnabled = true
                binding.btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))

            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE->{
                //Log.d("My_APP_TAG","지문인식을 지원하지 않는 기기입니다.")
                binding.tvMsg.text = "지문인식을 지원하지 않는 기기입니다."
                binding.btnLogin.isEnabled = false
                binding.btnLogin1.isEnabled = true
                binding.btnLogin1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED->{
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                  putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                }
                binding.btnLogin.isEnabled = false

                startActivityForResult(enrollIntent, 100)
            }
        }



    }

}