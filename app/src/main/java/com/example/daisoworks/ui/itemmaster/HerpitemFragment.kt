package com.example.daisoworks.ui.itemmaster


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.daisoworks.data.CorpCd
import com.example.daisoworks.data.DataItemDetail1
import com.example.daisoworks.data.DataItemDetail2
import com.example.daisoworks.data.DataItemDetail3
import com.example.daisoworks.data.DataItemDetail4
import com.example.daisoworks.ExpandableAdapterHerpItem1
import com.example.daisoworks.ExpandableAdapterHerpItem2
import com.example.daisoworks.ExpandableAdapterHerpItem3
import com.example.daisoworks.ExpandableAdapterHerpItem4
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.R
import com.example.daisoworks.data.ItemCount
import com.example.daisoworks.databinding.FragmentHerpitemBinding
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class HerpitemFragment : Fragment() {

    private var _binding: FragmentHerpitemBinding? = null
    private val binding get() = _binding!!

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService

    //상품기본정보등 화면 Data 및 Adapter
    private var itemList1 = ArrayList<DataItemDetail1>()
    private var itemList2 = ArrayList<DataItemDetail2>()
    private var itemList3 = ArrayList<DataItemDetail3>()
    private var itemList4 = ArrayList<DataItemDetail4>()
    private lateinit var ExpandableAdapterHerpItem1: ExpandableAdapterHerpItem1
    private lateinit var ExpandableAdapterHerpItem2: ExpandableAdapterHerpItem2
    private lateinit var ExpandableAdapterHerpItem3: ExpandableAdapterHerpItem3
    private lateinit var ExpandableAdapterHerpItem4: ExpandableAdapterHerpItem4

    //spinner 값 배열리스트 초기화
    private val comCdDataArr = mutableListOf<String>("선택하세요")
    //spinner는 id,value 형태가 아니라서 value만 들어감. 그래서 Map형태로 API통신시 미리 두개를 가져오(한개는 spinner값, 검색버튼 누를때 spinner값을 코드로 치환해주기 위한 Map임.
    private var mutableComMap = mutableMapOf<String, String>()
    //spinner의 값을 가지고 위의 배열에서 바이어시디코드를 저장함.
    private  var BuyerCd : String = ""
    private var comCd : String = ""

    private val itemcnt = mutableListOf<Long>()

    //내부저장소 변수 설정
    companion object{
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val herpitemViewModel =
            ViewModelProvider(this).get(HerpitemViewModel::class.java)

        _binding = FragmentHerpitemBinding.inflate(inflater, container, false)

        val root: View = binding.root
        prefs = PreferenceUtil(requireContext())

        //로그인시 담아놓은 회사코드를 가지고  API통신시 파라미터값으로 활용함.
         comCd = prefs.getString("companycode","0")

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        //API서비스 호출 파라미터 comCd 회사코드 같이 날려서 함수 실행
        getCorpCdList(supplementService,comCd)

        //데이터와 스피너를 연결 시켜줄 adapter를 만들어 준다.
        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
        //comCdDataArr는 list 변수이며 , lateinit을 통해 초기생성후 API통신 가져온값을 가지고 채움.
        var adapter = context?.let { ArrayAdapter<String>(it, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,comCdDataArr) }
        //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
        binding.spitemschbuyer.adapter = adapter


        //with scope funciton
        with(binding){
            spitemschbuyer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selected = comCdDataArr.get(position)
                   // binding.result.text = selected
                    Log.d("testtest" , "${selected}")

                    // Map값중 일치하는 것으 찾아 리턴함.
                    fun <K, V> getKey(map: Map<K, V>, target: V): K? {
                        for ((key, value) in map)
                        {
                            if (target == value) {
                                return key
                            }
                        }
                        return null
                    }

                     //선택된 Corp_cd 의 코드값
                    BuyerCd = getKey(mutableComMap, selected).toString()


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        } // with scope function end




        binding.svItem.setQueryHint("품번 입력")
        binding.svItem.isSubmitButtonEnabled = true

        //지금은 하드코딩, API완성시 별도 기술할 예정임.
        Glide.with(this).load("https://cdn.daisomall.co.kr/file/PD/20240708/fDLihH42tRGSTqojDpSQ1029927_00_00fDLihH42tRGSTqojDpSQ.jpg/dims/optimize/dims/resize/150x200")
            .into(binding.root.findViewById(com.example.daisoworks.R.id.photoView1))

        //상품정보 바인딩
        binding.rvHerpItemlist1.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem1 = ExpandableAdapterHerpItem1(itemList1)
        binding.rvHerpItemlist1.adapter = ExpandableAdapterHerpItem1

        //거래처정보 바인딩
        binding.rvHerpItemlist2.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem2 = ExpandableAdapterHerpItem2(itemList2,this)
        binding.rvHerpItemlist2.adapter = ExpandableAdapterHerpItem2

        //담당부서 바인딩
        binding.rvHerpItemlist3.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem3 = ExpandableAdapterHerpItem3(itemList3)
        binding.rvHerpItemlist3.adapter = ExpandableAdapterHerpItem3

        //샘플정보 바인딩
        binding.rvHerpItemlist4.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpItem4 = ExpandableAdapterHerpItem4(itemList4)
        binding.rvHerpItemlist4.adapter = ExpandableAdapterHerpItem4




        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                //validation Check(BuyerCd,corpcd,itemcode
                if(BuyerCd.isNullOrEmpty()){
                    Toast.makeText(requireContext(),"바이어를 선택하세요", Toast.LENGTH_SHORT).show()
                    return false
                }else if (comCd.isNullOrEmpty()){
                    Toast.makeText(requireContext(),"회사정보가 누락되었습니다.재로그인해주세요", Toast.LENGTH_SHORT).show()
                    return false
                }else if(query.isNullOrEmpty()){
                    Toast.makeText(requireContext(),"품번을 등록해주세요", Toast.LENGTH_SHORT).show()
                    return false
                }else {
                    //조회품번이 1개이상인지 체크함.
                    getItemCount(supplementService,comCd, BuyerCd, query)



                    binding.svItem.clearFocus()
                    Log.d("testtest", "===검색시작===")

                  /*  if(query=="1000382"){

                    if( binding.photoView1.visibility == GONE) {
                        binding.photoView1.visibility = VISIBLE
                    }

                    if( binding.rvHerpItemlist1.visibility == GONE) {
                        binding.rvHerpItemlist1.visibility = VISIBLE
                    }

                    if( binding.rvHerpItemlist2.visibility == GONE) {
                        binding.rvHerpItemlist2.visibility = VISIBLE
                    }

                    if( binding.rvHerpItemlist3.visibility == GONE) {
                        binding.rvHerpItemlist3.visibility = VISIBLE
                    }

                    if( binding.rvHerpItemlist4.visibility == GONE) {
                        binding.rvHerpItemlist4.visibility = VISIBLE
                    }


                    getData1()
                    getData2()
                    getData3()
                    getData4()

                    Toast.makeText(requireContext(),"품번 $query 검색", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(),"품번 $query 이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }*/
                    return true
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                return true
            }
        })




        return root
    }

    private fun getData1() {
        // 서버에서 가져온 데이터라고 가정한다.
        /*itemNo,barcodeNo,itemStorePrice,itemCategory,itemDesc,itemGrade,itemSalesLead,itemIpsu,itemDetail,itemPictureUrl,clientNoP,clientPreNoP,clientNameP,
        ,clientNoB,clientPreNoB,clientNameB,clientNoS,clientPreNoS,clientNameS,
        ownerDeptName,ownerName,ownerCompany,businessOwnerDept,businessOwnerName,shipmentDept,shipmentOwnerName,sampleNewItemNo,sampleNItemNo,sampleCsNoteNo
        sampleCsNoteItemNo,exhName,exhPeriod, exhDetail*/
        val itemListData1 = DataItemDetail1(
            "1000382",
            "8819910003825",
            "매장가: ￦ 2,000",
            "조리/식사>조리도구>식도",
            "BH컬러손잡이식도",
            "상품등급: B",
            "매출주도: ",
            "입수:" + " 12 × 4",
            "상세: 일반상품",
            ""
            /*  "201925",
            "CP2017040028",
            "CREATE COMMERCE CO., LIMITED",
            "201925",
            "CP2017040028",
            "CREATE COMMERCE CO., LIMITED",
            "201925",
            "CP2017040028",
            "CREATE COMMERCE CO., LIMITED",
            "상품개발부",
            "이덕기",
            "아성HMP",
            "조리/식사용품팀",
            "신종선",
            "심천(조리/식사용품팀)",
            "허철호",
            "NB2016050056",
            "NA20160401272",
            "BN2016040014",
            "CN20160400760",
            "16'  광주 1차",
            "2016-06-25~2016-06-28",
            "주방 도구 페어"*/
        )

        // 상품정보
        itemList1.clear()
        itemList1.add(itemListData1)
        ExpandableAdapterHerpItem1.notifyDataSetChanged()

    }

       /* private fun getData2() {
            // 서버에서 가져온 데이터라고 가정한다.
            *//*itemNo,barcodeNo,itemStorePrice,itemCategory,itemDesc,itemGrade,itemSalesLead,itemIpsu,itemDetail,itemPictureUrl,clientNoP,clientPreNoP,clientNameP,
            ,clientNoB,clientPreNoB,clientNameB,clientNoS,clientPreNoS,clientNameS,
            ownerDeptName,ownerName,ownerCompany,businessOwnerDept,businessOwnerName,shipmentDept,shipmentOwnerName,sampleNewItemNo,sampleNItemNo,sampleCsNoteNo
            sampleCsNoteItemNo,exhName,exhPeriod, exhDetail*//*
            val itemListData2 = DataItemDetail2(
                *//* "1000382",
               "8819910003825",
               "매장가: ￦ 2,000",
               "조리/식사>조리도구>식도",
               "BH컬러손잡이식도",
               "상품등급: B",
               "매출주도: ",
               "입수:" + " 12 × 4",
               "상세: 일반상품",
               "",*//*
               "201925",
                 "CP2017040028",
                 "CREATE COMMERCE CO., LIMITED",
                 "201925",
                 "CP2017040028",
                 "CREATE COMMERCE CO., LIMITED",
                 "201925",
                 "CP2017040028",
                 "CREATE COMMERCE CO., LIMITED"
              *//*   "상품개발부",
                 "이덕기",
                 "아성HMP",
                 "조리/식사용품팀",
                 "신종선",
                 "심천(조리/식사용품팀)",
                 "허철호",
                 "NB2016050056",
                 "NA20160401272",
                 "BN2016040014",
                 "CN20160400760",
                 "16'  광주 1차",
                 "2016-06-25~2016-06-28",
                 "주방 도구 페어"*//*
            )

                // 거래처정보
                itemList2.clear()
                itemList2.add(itemListData2)
                ExpandableAdapterHerpItem2.notifyDataSetChanged()
    }*/
        private fun getData3() {
            // 서버에서 가져온 데이터라고 가정한다.
            /*itemNo,barcodeNo,itemStorePrice,itemCategory,itemDesc,itemGrade,itemSalesLead,itemIpsu,itemDetail,itemPictureUrl,clientNoP,clientPreNoP,clientNameP,
            ,clientNoB,clientPreNoB,clientNameB,clientNoS,clientPreNoS,clientNameS,
            ownerDeptName,ownerName,ownerCompany,businessOwnerDept,businessOwnerName,shipmentDept,shipmentOwnerName,sampleNewItemNo,sampleNItemNo,sampleCsNoteNo
            sampleCsNoteItemNo,exhName,exhPeriod, exhDetail*/
            val itemListData3 = DataItemDetail3(
                /* "1000382",
               "8819910003825",
               "매장가: ￦ 2,000",
               "조리/식사>조리도구>식도",
               "BH컬러손잡이식도",
               "상품등급: B",
               "매출주도: ",
               "입수:" + " 12 × 4",
               "상세: 일반상품",
               "",
                "201925",
                "CP2017040028",
                "CREATE COMMERCE CO., LIMITED",
                "201925",
                "CP2017040028",
                "CREATE COMMERCE CO., LIMITED",
                "201925",
                "CP2017040028",
                "CREATE COMMERCE CO., LIMITED",*/
               "상품개발부",
                   "이덕기",
                   "아성HMP",
                   "조리/식사용품팀",
                   "신종선",
                   "심천(조리/식사용품팀)",
                   "허철호"
                /*   "NB2016050056",
              "NA20160401272",
                "BN2016040014",
                "CN20160400760",
                "16'  광주 1차",
                "2016-06-25~2016-06-28",
                "주방 도구 페어"*/
            )

            // 거래처정보
            itemList3.clear()
            itemList3.add(itemListData3)
            ExpandableAdapterHerpItem3.notifyDataSetChanged()
        }

        private fun getData4() {
            // 서버에서 가져온 데이터라고 가정한다.
            /*itemNo,barcodeNo,itemStorePrice,itemCategory,itemDesc,itemGrade,itemSalesLead,itemIpsu,itemDetail,itemPictureUrl,clientNoP,clientPreNoP,clientNameP,
            ,clientNoB,clientPreNoB,clientNameB,clientNoS,clientPreNoS,clientNameS,
            ownerDeptName,ownerName,ownerCompany,businessOwnerDept,businessOwnerName,shipmentDept,shipmentOwnerName,sampleNewItemNo,sampleNItemNo,sampleCsNoteNo
            sampleCsNoteItemNo,exhName,exhPeriod, exhDetail*/
            val itemListData4 = DataItemDetail4(
                /* "1000382",
               "8819910003825",
               "매장가: ￦ 2,000",
               "조리/식사>조리도구>식도",
               "BH컬러손잡이식도",
               "상품등급: B",
               "매출주도: ",
               "입수:" + " 12 × 4",
               "상세: 일반상품",
               "",
                "201925",
                "CP2017040028",
                "CREATE COMMERCE CO., LIMITED",
                "201925",
                "CP2017040028",
                "CREATE COMMERCE CO., LIMITED",
                "201925",
                "CP2017040028",
                "CREATE COMMERCE CO., LIMITED",
                "상품개발부",
                "이덕기",
                "아성HMP",
                "조리/식사용품팀",
                "신종선",
                "심천(조리/식사용품팀)",
                "허철호", */
                "NB2016050056",
              "NA20160401272",
                "BN2016040014",
                "CN20160400760",
                "16'  광주 1차",
                "2016-06-25~2016-06-28",
                "주방 도구 페어"
            )

            // 샘플정보
            itemList4.clear()
            itemList4.add(itemListData4)
            ExpandableAdapterHerpItem4.notifyDataSetChanged()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //Retrofit Object 설정
    object RetrofitClient {
        private var instance: Retrofit? = null
        private val gson = GsonBuilder().setLenient().create()

        //BASEURL 끝에 / 빠지면 에러 남.
        private const val BASE_URL = "http://10.55.55.40:3000/"

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
        @GET("buyersch")
        fun corpCd(
            @Query("comCode") param1: String
        ): Call<List<CorpCd>>

        @GET("itemcount")
        fun itemCount(
            @Query("comCode") param1: String,
            @Query("buyCode") param2: String,
            @Query("querytxt") param3: String
        ): Call<List<ItemCount>>

    }

    // API RETROFIT2 호출 함수 구현( HR에서 제공해준 API주소에 인자값 셋팅 , GET방식, 파라미터1, 2 는 HR에서 제공해준값 ,파라미터 3,4 는 앱에서 던짐.)
    // Callback시 GSON 형식에 맞게끔 데이터 클래스 담아야 함.여기서 에러주의, Json 형식 맞아야하고 GSON 변환도 맞아야함.
    private fun getCorpCdList(service: RetrofitService, keyword1:String){
        service.corpCd(keyword1).enqueue(object: retrofit2.Callback<List<CorpCd>> {
            override  fun onFailure(call: Call<List<CorpCd>>, error: Throwable) {
                Log.d("CorpCd", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<CorpCd>>,
                response: Response<List<CorpCd>>
            ) {
                Log.d("testtest" , "success")
                // RetroFit2 API CorpCd 결과값중 VALUE값 셋팅
              //  var  loginFlag: String? = response.body()?.loginList?.get(0)?.VALUE?.toString()

                val responseBody = response.body()!!
                //val myStringBuilder = StringBuilder()
                for(comCdData1 in responseBody) {
                    comCdDataArr.add( comCdData1.comName)
                    mutableComMap.put(comCdData1.comCd , comCdData1.comName)

                }
            }
        })
    }

    fun createDialog(){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.itemdouble, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("품번 선택")

        mBuilder.show()
    }
    //Item 갯수 체크
    private fun getItemCount(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.itemCount(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<ItemCount>> {
            override  fun onFailure(call: Call<List<ItemCount>>, error: Throwable) {
                Log.d("itemcount", "실패원인: {$error}")
            }
            override fun onResponse(
                call: Call<List<ItemCount>>,
                response: Response<List<ItemCount>>
            ) {
            val responseBody = response.body()!!
                for(itemcnt1 in responseBody) {
                    itemcnt.add( itemcnt1.itemcnt)
                }
                Toast.makeText(requireContext(),"조회하시는 품번이 ${itemcnt[0]}개 검색되었습니다.", Toast.LENGTH_SHORT).show()
               if(itemcnt[0] > 1) {
                   createDialog()
                }
                Log.d("itemcount" , "success")
            }
        })
    }


}