package com.example.daisoworks


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.media3.common.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.daisoworks.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.google.firebase.messaging.remoteMessage
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicInteger


class MainActivity : AppCompatActivity()  {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var mBackWait:Long = 0
    lateinit var drawerLayout: DrawerLayout

    companion object{
        lateinit var prefs: PreferenceUtil
        private const val TAG = "MainActivity"
        private const val NOTIFICATION_REQUEST_CODE = 1234
    }

    private  var autoLoginFlag: String = ""
    private  var backFlag: String = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate(savedInstanceState)

        getFCMToken()
      //  initNavigationMenu()
        // [START handle_data_extras]
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }
        // [END handle_data_extras]


        // 1. 바인딩 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)

        // 2. 레이아웃(root뷰) 표시
        setContentView(binding.root)

        //3.액션 바를 설정
        setSupportActionBar(binding.appBarMain.toolbar)

        //5.drawerLayout Binding
        val drawerLayout: DrawerLayout = binding.drawerLayout

        //6.NavigationView Binding
        val navView: NavigationView = binding.navView

        //7.destination이 될 fragment 목록 설정
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_analysis,
                R.id.nav_approve,
                R.id.nav_analysis1,
                R.id.nav_approve1
            ), drawerLayout
        )

        //8.NavController를 host가 될 fragment(content_main 내에 있음)로부터 불러옴
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        //NavController와 AppBarConfiguration에 맞춰 ActionBar를 설정
        //(destination, 즉 부분 화면이 바뀔 때마다 ActionBar의 title을 변경해줌)
        setupActionBarWithNavController(navController, appBarConfiguration)

        //NavigationView를 NavController가 사용할 수 있도록 설정
        //NavigationView의 item 클릭 시 fragment가 변경되도록 설정해줌
        navView.setupWithNavController(navController)

        // BottomNavigationView 바인딩
        val navView1: BottomNavigationView = binding.navView1
        // NavController를 host가 될 fragment(content_main 내에 있음)로부터 불러옴
        //val navController1 = findNavController(R.id.nav_host_fragment_activity_main)

        // setupActionBarWithNavController(navController1, appBarConfiguration1)
        navView1.setupWithNavController(navController)

        autoLoginFlag = prefs.getString("autoLoginFlagS", "0")

        val currentFragmentFirst =
        supportFragmentManager.fragments.last().childFragmentManager?.primaryNavigationFragment?.tag//호스트 프래그먼트 가져오기
        prefs.setString("currentFragmentFirst", "$currentFragmentFirst")





    }

    // onCreateOptionsMenu() 메소드를 오버라이딩해서, getMenuInflater().inflate()로 옵션 메뉴를 객체화시킨다.
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        when(item?.itemId){
            R.id.action_settings1 -> {

                prefs.setString("autoLoginFlagS","0")
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                //ContextCompat.startActivity(binding.root.context, intent, null)
                return true
            } else -> return super.onOptionsItemSelected(item)
        }

    }


    //onSupportNavigateUp() 메소드를 오버라이딩해서, AppBar에 생성되는 뒤로가기 버튼을 눌렀을 때 뒤로 이동시킨다.
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onBackPressed() {

        val currentFragmentFirst1 = prefs.getString("currentFragmentFirst", "0")
        val currentFragment = supportFragmentManager.fragments.last().childFragmentManager?.primaryNavigationFragment?.tag.toString()//호스트 프래그먼트 가져오기
        var fragment1 = supportFragmentManager.findFragmentByTag("HOME")

        if(currentFragmentFirst1.equals(currentFragment)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃 안내")
            builder.setMessage("로그아웃 하시겠습니까?자동로그인이 초기화 됩니다")
            builder.setPositiveButton("확인") { dialog, which ->
                prefs.setString("autoLoginFlagS", "0")
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            builder.setNegativeButton("취소") { dialog, which ->
                Toast.makeText(this@MainActivity, "취소하였습니다.", Toast.LENGTH_SHORT).show()

            }
            builder.show()

        }else{
            super.onBackPressed();

        }
    }






    //PushMesssaging Service
    fun runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        Firebase.messaging.isAutoInitEnabled = true
        // [END fcm_runtime_enable_auto_init]
    }

    fun deviceGroupUpstream() {
        // [START fcm_device_group_upstream]
        val to = "a_unique_key" // the notification key
        val msgId = AtomicInteger()
        Firebase.messaging.send(
            remoteMessage(to) {
                setMessageId(msgId.get().toString())
                addData("hello", "world")
            },
        )
        // [END fcm_device_group_upstream]
    }

    fun sendUpstream() {
        val SENDER_ID = "YOUR_SENDER_ID"
        val messageId = 0 // Increment for each
        // [START fcm_send_upstream]
        val fm = Firebase.messaging
        fm.send(
            remoteMessage("$SENDER_ID@fcm.googleapis.com") {
                setMessageId(messageId.toString())
                addData("my_message", "Hello World")
                addData("my_action", "SAY_HELLO")
            },
        )
        // [END fcm_send_upstream]
    }

    fun subscribeTopics() {
        // [START subscribe_topics]
        Firebase.messaging.subscribeToTopic("weather")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]
    }

    fun logRegToken() {
        // [START log_reg_token]
        Firebase.messaging.getToken().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "FCM Registration token: $token"
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
        // [END log_reg_token]
    }

    // [START ask_post_notifications]
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // [END ask_post_notifications]

    // [START get_store_token]
    private suspend fun getAndStoreRegToken(): String {
        val token = Firebase.messaging.token.await()
        // Add token and timestamp to Firestore for this user
        val deviceToken = hashMapOf(
            "token" to token,
            "timestamp" to FieldValue.serverTimestamp(),
        )

        // Get user ID from Firebase Auth or your own server
        Firebase.firestore.collection("fcmTokens").document("myuserid")
            .set(deviceToken).await()
        return token
    }
    // [END get_store_token]
    private fun getFCMToken(): String?{
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log and toast
            Log.d(TAG, "FCM Token is ${token}")
        })

        return token
    }


}

