package com.example.daisoworks.ui.sample

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.MainActivity.Companion.prefs
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.adapter.AdapterSampList
import com.example.daisoworks.adapter.ItemClickListener
import com.example.daisoworks.data.DataSampleList
import com.example.daisoworks.databinding.FragmentSample1Binding
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

class sample1Fragment : Fragment() , ItemClickListener ,BottomSheetFragment.BottomSheetDismissListener {

    companion object {
        fun newInstance() = sample1Fragment()
    }

    private val viewModel: Sample1ViewModel by viewModels()
    private var memempmgnum: String = "" // 상담자
    private var memdeptcde: String = "" // 상담자부서코드
    private var _binding: FragmentSample1Binding? = null
    private var samSdate : String = ""     // 샘플접수조회시작일자
    private var samEdate : String = ""     // 샘플접수조회종료일자
    private val binding get() = _binding!!
    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService
    private var sampleList = ArrayList<DataSampleList>()
    private lateinit var AdapterSampleList: AdapterSampList
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSample1Binding.inflate(inflater, container, false)

        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceUtil(requireContext())

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        memempmgnum = prefs.getString("memempmgnum", "none")
        memdeptcde = prefs.getString("memdeptcde", "none")

        val cal = Calendar.getInstance()
        val now: LocalDateTime = LocalDateTime.now()
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        var sevendays = (now.plusDays(-6)).format(formatter).toString()

        binding.txtcousdate.setText(sevendays.format(formatter).toString())
        binding.txtcouedate.setText(now.format(formatter).toString())


        binding.txtcousdate.setOnClickListener{
            keyBordHide(requireContext() , it)
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->

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
                samSdate  =  "$y-${mmonth}-$dday"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }


        binding.txtcouedate.setOnClickListener{
            keyBordHide(requireContext(), it)
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->

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
                samEdate  =  "$y-${mmonth}-$dday"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        var samSdate1 =  binding.txtcousdate.text
        var samEdate1 = binding.txtcouedate.text
        getSamList(supplementService, "${memempmgnum}","${samSdate1}","${samEdate1}","${BuildConfig.API_KEY}")

        binding.samSearch.setOnClickListener {
            var samSdate1 =  binding.txtcousdate.text
            var samEdate1 = binding.txtcouedate.text
            getSamList(supplementService, "${memempmgnum}","${samSdate1}","${samEdate1}","${BuildConfig.API_KEY}")
        }


        // FloatingActionButton 클릭 이벤트
        // val sbtn = findViewById<Button>(R.id.exhRegist)
        binding.homeGoalAddFloatingBtn.setOnClickListener {
            //Log.d("testtestset" , "click")
            //  val id = etId.text.toString()
            //val intent = Intent(this, ExhibitionWriteActivity::class.java)
            //  intent.putExtra("id", id)
            // startActivity(intent)
            val bottomSheetFragment = BottomSheetFragment(this)


            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)

        }

    }

    private fun getSamList(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String){
        service.sampleList(keyword1,keyword2,keyword3,keyword4).enqueue(object:
            Callback<List<DataSampleList>> {
            override  fun onFailure(call: Call<List<DataSampleList>>, error: Throwable) {
                Log.d("ExhibitionMyList", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSampleList>>,
                response: Response<List<DataSampleList>>
            ) {
                val responseBody = response.body()!!
                sampleList.clear()

                for (comCdData1 in responseBody) {
                    sampleList.add(DataSampleList(comCdData1.sammgno, comCdData1.samnm, comCdData1.salercpdte))

                }

                Log.d("sampleListcount", sampleList.size.toString())
                AdapterSampleList = AdapterSampList(sampleList , this@sample1Fragment)
                binding.root.findViewById<RecyclerView>(com.example.daisoworks.R.id.rv_samlist).adapter = AdapterSampleList

            }
        })
    }

    fun showAlert(str1: String ,str2: String , str3: String ){

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(str1)
        builder.setMessage(str2)
        builder.setPositiveButton(str3, DialogInterface.OnClickListener { dialog, which ->
            //    Toast.makeText(this, str3, Toast.LENGTH_SHORT).show()
        })

        builder.create()
        builder.show()
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

    private fun keyBordHide(context: Context, view: View) {
     val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
     imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



    interface RetrofitService {
        @GET("samplelist")
        fun sampleList(
            @Query("memempmgnum") param1: String,
            @Query("startdate") param2: String,
            @Query("enddate") param3: String,
            @Query("apikey") param5: String
        ): Call<List<DataSampleList>>


        @GET("sampleunregist")
        fun sampleUnregist(
            @Query("samplecode") param1: String,
            @Query("memempmgnum") param2: String,
            @Query("apikey") param3: String
        ): Call<String>

    }
    override fun onItemClicked(position: Int) {
        // 클릭된 아이템 처리
        var kstr1 = sampleList[position].sammgno
        //Toast.makeText(requireContext(), "Clicked position: $kstr1", Toast.LENGTH_SHORT).show()
        if(kstr1.isNotEmpty()){
            showConfirmDialog(kstr1)
        }


    }

    private fun showConfirmDialog(kstr1:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("삭제확인")
        builder.setMessage("정말 삭제하시겠습니까?")

        builder.setPositiveButton("예") { _, _ ->

            performDelete(kstr1)
        }

        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun performDelete(kstr1: String) {
        var samplenum = kstr1
        if(samplenum.isNotEmpty()) {
            unregistSample(supplementService, samplenum, memempmgnum,"${BuildConfig.API_KEY}")
        }

    }

    private fun unregistSample(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.sampleUnregist(keyword1,keyword2,keyword3).enqueue(object:
            Callback<String> {
            override  fun onFailure(call: Call<String>, error: Throwable) {
                Log.d("unregistSample", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
              //  val responseBody = response.body()!!
                if(response.body()=="ok") {
                    var samSdate1 =  binding.txtcousdate.text
                    var samEdate1 = binding.txtcouedate.text
                    getSamList(supplementService, "${memempmgnum}","${samSdate1}","${samEdate1}","${BuildConfig.API_KEY}")
                }
            }
        })
    }


    // BottomSheet가 닫힐 때 실행할 함수
    override fun onBottomSheetDismissed() {
        var samSdate1 =  binding.txtcousdate.text
        var samEdate1 = binding.txtcouedate.text
       // Toast.makeText(requireContext(), "BottomSheet 닫힘!", Toast.LENGTH_SHORT).show()
        getSamList(supplementService, "${memempmgnum}","${samSdate1}","${samEdate1}","${BuildConfig.API_KEY}")
    }



}