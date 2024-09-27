package com.example.daisoworks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.adapter.ExpandableAdapter
import com.example.daisoworks.adapter.ExpandableAdapter1
import com.example.daisoworks.adapter.ExpandableAdapterDmsNotice
import com.example.daisoworks.data.CorpId
import com.example.daisoworks.data.DataItem1
import com.example.daisoworks.databinding.FragmentTab2Binding
//import com.example.daisoworks.ui.itemmaster.HerpitemFragment
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

    private var param1: String? = null
    private var param2: String? = null

    private var languageList = ArrayList<CorpId>()
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

        //로그인시 담아놓은 회사코드를 가지고  API통신시 파라미터값으로 활용함.
        comId = prefs.getString("mUserId","AD2201012")

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        //배열 초기화
        languageList = ArrayList<CorpId>()
        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getData(supplementService,"${BuildConfig.API_KEY}",comId)
        //getData()

        languageList1 = ArrayList<DataItem1>()
        getData1()

        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        expandableAdapterDmsNotice = ExpandableAdapterDmsNotice(languageList)

        // define layout manager for the Recycler view
        // binding.root.findViewById<RecyclerView>(R.id.rv_list).layoutManager = LinearLayoutManager(requireContext())
        // attach adapter to the recyclerview
        binding.rvList1.layoutManager = LinearLayoutManager(requireContext())
        expandableAdapter1 = ExpandableAdapter1(languageList1)

        binding.root.findViewById<RecyclerView>(R.id.rv_list).adapter = expandableAdapterDmsNotice
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
                Log.d("MELONG1" , " getItemList1 성공 시작")
                //languageList = response.body()
               // Log.d("MELONG2", languageList.toString())
                val responseBody = response.body()!!
              //  Log.d("MELONG2", responseBody.toString())
                languageList.clear()
                for (comCdData1 in responseBody) {

                     // Log.d("MELONG3" , comCdData1.boardTitle)
                    // `boardTitle`과 `boardContents`를 바로 `CorpId`에 추가
                    val innerList1 = CorpId(comCdData1.boardTitle, comCdData1.boardContents, false)
                    languageList.add(innerList1) // CorpId에 필요한 값을 전달*/
                   // Log.d("languageList888" , innerList1.toString())
                }

            }
        })
    }

    /*

    private fun getData() {
        // 서버에서 가져온 데이터라고 가정한다.
        // create new objects and add some row data
        val language1 = DataItem(
            "2024.04.25 DMS 시스템 기능 업데이트 공지",
            "변경 사항:\n" +
                    "디자인 진행현황[업무표준화팀] 프로그램 추가\n" +
                    "대상 : TQC -업무표준화팀\n" +
                    "내용 : RRP 디자인 의뢰 진행 현황 조회\n" +
                    "퇴사자 데이터변경 기능 수정\n" +
                    "대상 : 기존 퇴사자 데이터변경 프로그램 사용자 모두\n" +
                    "내용 :\n" +
                    "사용자와 같은 회사의 속한 담당자만 변경 가능\n" +
                    "진행 중이지 않은 의뢰(발주, 캔슬된)는 담당자 변경 불가능\n" +
                    "기타 etc\n" +
                    "업무에 참고하시기 바랍니다.\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "관련 문의는 Desk게시판에 올려주시면 답변 드리도록 하겠습니다.\n" +
                    "\n" +
                    "감사합니다." ,
            false
        )
        val language2 = DataItem(
            "[DMS 기능 업데이트 안내] 진행현황 (관리자) 프로그램",
            "- 프로그램 위치 : 디자인의뢰/진행 - 디자인진행 \n" +
                    "\n" +
                    "- 프로그램 명 : 진행현황 (관리자)\n" +
                    "\n" +
                    "- 주요 내용\n" +
                    "\n" +
                    "   1. 일괄 디자인캔슬(미수주) 기능 추가 (개별 기능과 조건은 동일함)\n" +
                    "\n" +
                    "      ① 디자인 담당자 지정 이후 ~ 발주전 디자인캔슬(미수주) 처리 가능\n" +
                    "\n" +
                    "      ② 의뢰 접수자 용으로 기능 권한이 제한되어 있습니다\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "업무에 참고하시기 바랍니다.\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "관련 문의는 Desk게시판에 올려주시면 답변 드리도록 하겠습니다.\n" +
                    "\n" +
                    "감사합니다." ,
            false
        )
        val language3 = DataItem(
            "DMS 접속 불가 시 확인사항",
            "1. 무선 Wifi 연결 여부 확인\n" +
                    "\n" +
                    "-> DMS는 유선 내부망으로만 연결 가능합니다. Wifi가 자동연결되어 잡히는 경우 접속이 불가능합니다.\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "2. 접속 브라우저 확인\n" +
                    "\n" +
                    "-> DMS는 윈도우/맥 환경 모두 크롬 브라우저를 별도 설치하여 접속하는 것을 권장합니다.\n" +
                    "\n" +
                    "-> 엣지의 경우 보안 프로그램과 충돌로 인해 브라우저 속도 지연이 생기는 것으로 추측됩니다.\n" +
                    "\n" +
                    "-> 크롬도 접속 지연이 생기는 경우 네이버 웨일 브라우저를 설치하여 접속해 보시기 바랍니다." ,
            false
        )
        val language4 = DataItem(
            "[DMS 계산바코드 생성 가이드]",
            "안녕하십니까, 심상모 과장입니다. \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "DMS에 계산바코드가 품번에 맞게 자동으로 생성되어 \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "원하는 품번의 계산바코드를 다운로드 받아\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "사용하실 수 있게 되었습니다. \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "기존 별도 프로그램을 통해 직접 생성하여 사용하던 방식에서\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "오류를 줄이고 효율적인 업무를 진행할 수 있도록 \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "개선되었으니, 업무에 참고하시기 바랍니다.\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "감사합니다.\n" +
                    "\n" +
                    " " ,
            false
        )
        val language5 = DataItem(
            "[DMS 계산바코드 생성 가이드]11",
            "안녕하십니까, 심상모 과장입니다. \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "DMS에 계산바코드가 품번에 맞게 자동으로 생성되어 \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "원하는 품번의 계산바코드를 다운로드 받아\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "사용하실 수 있게 되었습니다. \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "기존 별도 프로그램을 통해 직접 생성하여 사용하던 방식에서\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "오류를 줄이고 효율적인 업무를 진행할 수 있도록 \n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "개선되었으니, 업무에 참고하시기 바랍니다.\n" +
                    "\n" +
                    " \n" +
                    "\n" +
                    "감사합니다.\n" +
                    "\n" +
                    " " ,
            false
        )



        // add items to list
        languageList.add(language1)
        languageList.add(language2)
        languageList.add(language3)
        languageList.add(language4)
        languageList.add(language5)
*/

        //expandableAdapter.notifyDataSetChanged()


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