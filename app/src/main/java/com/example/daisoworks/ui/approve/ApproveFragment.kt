package com.example.daisoworks.ui.approve

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.adapter.MyAdapter
import com.example.daisoworks.data.Member
import com.example.daisoworks.databinding.FragmentApproveBinding
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class ApproveFragment : Fragment() {

    private var _binding: FragmentApproveBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var listStatus : String = "ALL"

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : ApproveFragment.RetrofitService
    private var id1 : String = ""
    var dataList1: List<Member>  = listOf()

    companion object{
        lateinit var prefs: PreferenceUtil
    }


    // RecyclerView 가 불러올 목록
    private var data:MutableList<Member> = mutableListOf()
    private var data1:MutableList<Member> = mutableListOf()
    var i = 4


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d("setData" , "start0")

        prefs = PreferenceUtil(requireContext())

        val approveViewModel =
            ViewModelProvider(this).get(ApproveViewModel::class.java)

        _binding = FragmentApproveBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textView2
        val layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, // CardView width
            ViewGroup.LayoutParams.WRAP_CONTENT // CardView height
        )



        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = ApproveFragment.RetrofitClient.getInstance()
        supplementService = retrofit.create(ApproveFragment.RetrofitService::class.java)

         id1 =  prefs.getString("id", "none")
        //id1 = "AH1506150"
      //  id1 = "AH0403070"
        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행



        getApproveList(supplementService,"${BuildConfig.API_KEY}",id1)



        layoutParams.setMargins(16,16,16,50)
        textView.text = "디자인진행 승인정보관리"


        val btnRQ = binding.butPro
        val btnAL = binding.butAll
        val btnCM = binding.butEnd


        btnRQ.setOnClickListener {
            listStatus = "RQ"
            getApproveList(supplementService,"${BuildConfig.API_KEY}",id1)
        }


        btnCM.setOnClickListener {
            listStatus = "CM"
            getApproveList(supplementService,"${BuildConfig.API_KEY}",id1)
        }


        btnAL.setOnClickListener {
            listStatus = "All"
            getApproveList(supplementService,"${BuildConfig.API_KEY}",id1)
        }


        return root
    }



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
        @GET("dmsview1")
        fun getdmsList(
            @Query("apikey") param1: String,
            @Query("mUserId") param2: String
        ): Call<List<Member>>

    }

    private fun getApproveList(service: ApproveFragment.RetrofitService, keyword1:String, keyword2:String){
        service.getdmsList(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<Member>> {

            override fun onFailure(call: Call<List<Member>>, error: Throwable) {
                Log.d("DataItemList2", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<Member>>,
                response: Response<List<Member>>
            ) {
                Log.d("DMS LIST", "1")

                val responseBody = response.body()!!
                Log.d("DMS LIST", "{$responseBody.size}")

                  data = mutableListOf()
                  data1 = mutableListOf()

                for (dmslist in responseBody) {
                    Log.d("DMS LIST", "3")
                    Log.d("DMS LIST", "$dmslist.reqNo")
                    data.add(
                        Member(
                            dmslist.reqId,
                            dmslist.revNo,
                            dmslist.apprSeq,
                            dmslist.productCd,
                            dmslist.korProductDesc,
                            dmslist.apprStts,
                            dmslist.mainDsnEmpNm,
                            dmslist.cmplExptDate
                        )
                    )
                }


                if (listStatus == "All") {
                    data1 = data

                } else if (listStatus == "RQ"){
                    data1 = data.filter { it.apprStts == "RQ" }.toMutableList()

                }else if (listStatus=="CM"){
                    data1 = data.filter { it.apprStts == "CM" }.toMutableList()
                }else{
                    data1 = data
                }



                val adapter = MyAdapter()
                adapter.listData = data1
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                //binding.recyclerView.notifyDataSetChanged()
                adapter.notifyDataSetChanged()
                Log.d("DMS LIST" , "5")

            }

        })
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
       // Log.d("aaaaaaonResume" , "5")

      //  data.clear();
      //  data1.clear();
     //   getApproveList(supplementService,"${BuildConfig.API_KEY}",id1)


    }


}