package com.example.daisoworks

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.daisoworks.databinding.ActivitySubBinding
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog

class SubActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySubBinding

    private var ReqNo: String = ""
    private var reqItemDesc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_sub)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)





        if (intent.hasExtra("reqNo")) {
            ReqNo = intent.getStringExtra("reqNo").toString()
            reqItemDesc = intent.getStringExtra("reqItemDesc").toString()


            Log.d("PUTVALUE" , "$ReqNo")

        }

        //기본 Actionbar 제목 변경
        getSupportActionBar()?.setTitle("$reqItemDesc");
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화 (화살표)


        var currentDestination = navController.currentDestination
       // androidx.media3.common.util.Log.d("testtest" , "$currentDestination")
      //  if(currentDestination.displayName == "Destination(com.example.daisoworks:id/nav_home) label=홈 class=com.example.daisoworks.ui.home.HomeFragment")

    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_sub)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
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