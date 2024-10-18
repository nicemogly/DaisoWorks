package com.example.daisoworks

//import com.example.daisoworks.ui.itemmaster.HerpitemFragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.adapter.ExpandableAdapter1
import com.example.daisoworks.adapter.ExpandableAdapterDmsNotice
import com.example.daisoworks.data.CorpId
import com.example.daisoworks.data.DataItem1
import com.example.daisoworks.databinding.FragmentTab2Binding
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class fragment_tab2 : Fragment() {

    private var _binding: FragmentTab2Binding? = null
    private val binding get() = _binding!!

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService

    //공지사항
    private  var languageList = ArrayList<CorpId>()
    private lateinit var expandableAdapterDmsNotice: ExpandableAdapterDmsNotice

    private var languageList1 = ArrayList<DataItem1>()
    private lateinit var expandableAdapter1: ExpandableAdapter1

    private var comId : String = ""

    //var data: DataItemDetail1? = null
    //var dataList1: List<DataItemDetail1>? = null

    //내부저장소 변수 설정
    companion object{
        lateinit var prefs: PreferenceUtil
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTab2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        prefs = PreferenceUtil(requireContext())

        //로그인시 담아놓은 ID를 가지고  API통신시 파라미터값으로 활용함.
        comId = LoginActivity.prefs.getString("id", "none")

        Log.d("MELONG" , comId)

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        //배열 초기화
        languageList = ArrayList<CorpId>()
        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getData(supplementService,"${BuildConfig.API_KEY}",comId)

        languageList1 = ArrayList<DataItem1>()
        getData1()

        binding.rvList.layoutManager = LinearLayoutManager(requireContext())

       // expandableAdapterDmsNotice.notifyDataSetChanged()
        // define layout manager for the Recycler view
        // binding.root.findViewById<RecyclerView>(R.id.rv_list).layoutManager = LinearLayoutManager(requireContext())
        // attach adapter to the recyclerview
        binding.rvList1.layoutManager = LinearLayoutManager(requireContext())
        expandableAdapter1 = ExpandableAdapter1(languageList1)

        binding.root.findViewById<RecyclerView>(R.id.rv_list1).adapter = expandableAdapter1

        return root
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

    //Retrofit Service : 전송방식(GET,POST....) , Parameter 세팅가능
    interface RetrofitService {
        @GET("dmsnotice")
        fun noticeView1(
            @Query("apikey") param1: String,
            @Query("mUserId") param2: String
        ): Call<List<CorpId>>

    }

    // API RETROFIT2 호출 함수 구현( HR에서 제공해준 API주소에 인자값 셋팅 , GET방식, 파라미터1, 2 는 HR에서 제공해준값 ,파라미터 3,4 는 앱에서 던짐.)
    // Callback시 GSON 형식에 맞게끔 데이터 클래스 담아야 함.여기서 에러주의, Json 형식 맞아야하고 GSON 변환도 맞아야함.
    private fun getData(service: RetrofitService, keyword1:String, keyword2: String){
        service.noticeView1(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<CorpId>> {
                    override  fun onFailure(call: Call<List<CorpId>>, error: Throwable) {
                        Log.d("CorpId", "문제실패원인: {$error}")
                    }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<CorpId>>,
                response: Response<List<CorpId>>
            ) {

                val responseBody = response.body()!!
                languageList.clear()
                for (comCdData1 in responseBody) {

                    languageList.add(CorpId(comCdData1.boardTitle, comCdData1.boardContents, false)) // CorpId에 필요한 값을 전달*/
                }
                expandableAdapterDmsNotice = ExpandableAdapterDmsNotice(languageList)
                binding.root.findViewById<RecyclerView>(R.id.rv_list).adapter = expandableAdapterDmsNotice

            }
        })
    }

    private fun getData1() {
        // 서버에서 가져온 데이터라고 가정한다.
        // create new objects and add some row data
        val language1 = DataItem1(
            "정현도",
            "2024.04.25 13:22:11" ,
            "디자인 내용확인 승인요청드립니다.1",
            false
        )
        val language2 = DataItem1(
            "이유용",
            "2024.04.26 13:22:11" ,
            "디자인 내용확인 승인요청드립니다.2",
            false
        )
        val language3 = DataItem1(
            "심상모",
            "2024.04.27 13:22:11" ,
            "디자인 내용확인 승인요청드립니다.3",
            false
        )
        val language4 = DataItem1(
            "장문영",
            "2024.05.25 13:22:11" ,
            "디자인 내용확인 승인요청드립니다.4",
            false
        )
        val language5 = DataItem1(
            "윤장훈",
            "2024.06.25 13:22:11" ,
            "디자인 내용확인 승인요청드립니다.5",
            false
        )



        // add items to list
        languageList1.add(language1)
        languageList1.add(language2)
        languageList1.add(language3)
        languageList1.add(language4)
        languageList1.add(language5)

        //expandableAdapter.notifyDataSetChanged()

    }



}