package com.example.daisoworks.ui.client

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.adapter.ExpandableAdapterHerpClient1
import com.example.daisoworks.adapter.ExpandableAdapterHerpClient2
import com.example.daisoworks.adapter.ExpandableAdapterHerpClient22
import com.example.daisoworks.data.ClientCount
import com.example.daisoworks.data.DataClientDetail1
import com.example.daisoworks.data.DataClientDetail2
import com.example.daisoworks.data.DataClientDetail22
import com.example.daisoworks.databinding.FragmentHerpclientBinding
import com.example.daisoworks.util.LoadingDialog
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class HerpclientFragment : Fragment() {

    private var _binding: FragmentHerpclientBinding? = null
    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : HerpclientFragment.RetrofitService

    private var ClientcodeP : String = ""
    private var ClientnameK : String = ""

    private val binding get() = _binding!!
    private var clientList1 = ArrayList<DataClientDetail1>()
    private var clientList2 = ArrayList<DataClientDetail2>()
    private lateinit var ExpandableAdapterHerpClient1: ExpandableAdapterHerpClient1
    private lateinit var ExpandableAdapterHerpClient2: ExpandableAdapterHerpClient2
    private lateinit var ExpandableAdapterHerpClient22: ExpandableAdapterHerpClient22
    private var comCd : String = ""


    private var clientcount = mutableListOf<ClientCount>()
    private var clientDialog1 = mutableListOf<String>()


    var dataList1: List<DataClientDetail1>? = null
    var dataList2: List<DataClientDetail2>? = null

    private val data21:MutableList<DataClientDetail22> = mutableListOf()
    companion object{
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHerpclientBinding.inflate(inflater, container, false)
        val root: View = binding.root


        HerpclientFragment.prefs = PreferenceUtil(requireContext())

        //로그인시 담아놓은 회사코드를 가지고  API통신시 파라미터값으로 활용함.
        comCd = HerpclientFragment.prefs.getString("companycode","0")



        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = HerpclientFragment.RetrofitClient.getInstance()
        supplementService = retrofit.create(HerpclientFragment.RetrofitService::class.java)


      //  initialize() // data 값 초기화
        var data = listOf("선택","거래처명","거래처코드")
        //데이터와 스피터를 연결 시켜줄 adapter를 만들어 준다.
        //ArrayAdapter의 두 번쨰 인자는 스피너 목록에 아이템을 그려줄 레이아웃을 지정.
        var adapter = context?.let { ArrayAdapter<String>(it, android.R.layout.simple_spinner_item,data) }
        //activity_main에서 만들어 놓은 spinner에 adapter 연결하여 줍니다.
        binding.spclientschCode.adapter = adapter

        binding.svClient.setQueryHint("거래처코드/명")
        binding.svClient.isSubmitButtonEnabled = true

        //거래처정보 바인딩
        binding.rvHerpClientlist1.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpClient1 = ExpandableAdapterHerpClient1(clientList1, context)
        binding.rvHerpClientlist1.adapter = ExpandableAdapterHerpClient1

        //거래처상품 껍데기 바인딩
        binding.rvHerpClientlist2.layoutManager = LinearLayoutManager(requireContext())
        ExpandableAdapterHerpClient2 = ExpandableAdapterHerpClient2( clientList2 , context)
        binding.rvHerpClientlist2.adapter = ExpandableAdapterHerpClient2


        binding.svClient.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.txtRstText.visibility = View.GONE
                var clientschMethod = binding.spclientschCode.getSelectedItem().toString()

                //Log.d("companysearch" , clientschMethod)
                // 검색 버튼 누를 때 호출
                binding.svClient.clearFocus()
                if(clientschMethod.equals("선택")) {
                    showAlert1("거래처명/코드를 선택하세요")
                    return false
                } else if (query.equals("null")){
                    showAlert1("거래처명/코드를 입력해주세요")
                    return false
                } else if (query.toString().length < 3 ){
                    showAlert1("검색어를 2글자이상 입력하세요")
                    return false
                }else {

                    if (binding.rvHerpClientlist1.visibility == View.GONE) {
                        binding.rvHerpClientlist1.visibility = View.VISIBLE
                    }

                    if (binding.rvHerpClientlist2.visibility == View.GONE) {
                        binding.rvHerpClientlist2.visibility = View.VISIBLE
                    }

                    showLoadingDialog()
                    var comNum:String = query.toString()

                    if(clientschMethod.equals("거래처코드")) {
                        getClientList1(supplementService, comCd, comNum, "${BuildConfig.API_KEY}")
                    }else if(clientschMethod.equals("거래처명")) {
                        Log.d("CSearch", "거래처명 검색 시작 ")
                        comNum =   "%"+comNum+"%"

                        Log.d("CSearch", comNum)
                        getClientCountList(supplementService, comCd, comNum,"${BuildConfig.API_KEY}")
                    //    getClientList11(supplementService, comCd, comNum,"${BuildConfig.API_KEY}")
                    }


                   Toast.makeText(requireContext(),"거래처 $query 검색", Toast.LENGTH_SHORT).show()
                   return true
                }

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색창에서 글자가 변경이 일어날 때마다 호출
                return true
            }
        })


        val args : HerpclientFragmentArgs by navArgs()
        val vcomno = args.comno

        if(vcomno=="") {

        }else{
            binding.spclientschCode.setSelection(2)
            binding.svClient.setQuery("$vcomno", true)
            binding.svClient.isIconified = false
            binding.spclientschCode.setSelection(2)

        }

        return root
    }


    fun createDialog(clientcount: MutableList<ClientCount>){

        val dataList =  mutableListOf<String>()

        for(i: Int in 0..clientcount.size-1){
            dataList.add(clientcount[i].clientBizNameK)
           // Log.d("CSearch", clientcount[i].clientBizNameK.toString())
        }
        Log.d("CSearch", "1")
        val adapter = ArrayAdapter<String>(
            requireContext(), android.R.layout.select_dialog_singlechoice ,dataList
        )

        Log.d("CSearch", "2")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("거래처 선택")
        // builder에 어뎁터 설정
        // 두 번째 매개변수에는 사용자가 선택한 항목의 순서값이 들어온다.
        builder.setAdapter(adapter){ dialogInterface: DialogInterface, i: Int ->
            // setTextView.text = "선택한 항목 : ${dataList[i]}"

            Log.d("CSearch", "3")
            ClientnameK =  dataList[i]
            ClientcodeP = clientcount[i].clientNoP.toString()

            clientGetData1()

        }

        Log.d("CSearch", "4")
        builder.setNegativeButton("취소",null)
        builder.show()
        binding.svClient.clearFocus()

    }



    private  fun clientGetData1() {

        //LoadingDialog(requireContext()).show()
        showLoadingDialog()
        Log.d("CSearch", ClientcodeP)
        getClientList1(supplementService, comCd, ClientcodeP,"${BuildConfig.API_KEY}")
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


        @GET("comview1")
        fun comView1(
            @Query("comCode") param1: String,
            @Query("comNum") param2: String,
            @Query("apikey") param3: String
        ): Call<List<DataClientDetail1>>

        @GET("comview11")
        fun comView11(
            @Query("comCode") param1: String,
            @Query("comNum") param2: String,
            @Query("apikey") param3: String
        ): Call<List<DataClientDetail1>>


        @GET("comview2")
        fun comView2(
            @Query("comCode") param1: String,
            @Query("comNum") param2: String,
            @Query("apikey") param3: String
        ): Call<List<DataClientDetail22>>
    }





    private fun showLoadingDialog() {
        val dialog = LoadingDialog(requireContext())
        CoroutineScope(Dispatchers.Main).launch {
            dialog.show()
            delay(1000)
            dialog.dismiss()
            // button.text = "Finished"
        }
    }

    private fun noitemDisplay(){
        binding.rvHerpClientlist1.visibility = View.GONE
        binding.rvHerpClientlist2.visibility = View.GONE
    }



    private fun showAlert1( str1:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(str1)
        builder.show()
    }
/*
    private fun getData2() {
        // 서버에서 가져온 데이터라고 가정한다.
        */
/*clientNoP,clientPreNoP,clientBizNoP,clientBizNameK,clientBizAddrK,clientBizCeoK,clientBizNameE,clientBizAddrE,clientBizCeoE,
        clientBizNameC,clientBizAddrC,clientBizCeoC,clientBizCountry,clientBizKind,clientBizTel,clientBizHomepage,clientBizEmail,expand1*//*


        val clientListData2 = DataClientDetail2(
            false ,
            data21


            )


       // clientList2.clear()
        clientList2.add(clientListData2)
        ExpandableAdapterHerpClient2.notifyDataSetChanged()
    }
*/




    private fun itemDisplay() {
        Log.d("ItevSearch" , "itemDisplay 호출")

        if( binding.rvHerpClientlist1.visibility == ConstraintSet.GONE) {
            binding.rvHerpClientlist1.visibility = View.VISIBLE
        }

        if( binding.rvHerpClientlist2.visibility == ConstraintSet.GONE) {
            binding.rvHerpClientlist2.visibility = View.VISIBLE
        }
        binding.svClient.clearFocus();
        binding.svClient.setQuery("", false)
    }


    private fun getClientCountList(service: HerpclientFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.comView11(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataClientDetail1>> {
            override  fun onFailure(call: Call<List<DataClientDetail1>>, error: Throwable) {
                Log.d("CSearch", "실패원인: {$error}")
            }
            override fun onResponse(
                call: Call<List<DataClientDetail1>>,
                response: Response<List<DataClientDetail1>>
            ) {
               // BuyerCd = keyword2
                Log.d("CSearch", "거래처명  API 성공 ")
                val responseBody = response.body()!!
                clientcount = mutableListOf<ClientCount>()
                clientDialog1 = mutableListOf<String>()
                for(clientcnt1 in responseBody) {
                    clientcount.add( ClientCount(clientcnt1.clientNoP , clientcnt1.clientBizNameK))
                    clientDialog1.add(clientcnt1.clientBizNameK)
                }
                Log.d("CSearch", clientcount.size.toString())
                if(clientcount.size > 1) { //거래처가 1개이상  존재할 경우
                    createDialog(clientcount)
                }else if(clientcount.size == 1) { // 거래처가 1개일 경우
                    ClientcodeP =  clientcount[0].clientNoP
                    if(ClientcodeP.isNotEmpty()) {
                        getClientList1(supplementService, comCd, ClientcodeP, "${BuildConfig.API_KEY}")
                    }
                }else { //거래처번호가 존재 하지 않을 경우

                    noitemDisplay()

                    binding.txtRstText.visibility = View.VISIBLE
                    binding.svClient.clearFocus();
                    binding.svClient.setQuery("", false)
                }

                ClientcodeP = binding.svClient.query.toString()
                Log.d("ItevSearch" , "거래처번호 + ${ClientcodeP}")

            }
        })
    }


private fun getClientList1(service: HerpclientFragment.RetrofitService, keyword1:String, keyword2:String, keyword3: String){
    service.comView1(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataClientDetail1>> {

        override  fun onFailure(call: Call<List<DataClientDetail1>>, error: Throwable) {
            Log.d("DataClientDetail1", "실패원인: {$error}")
        }

        //Retrofit error 없이 Response 떨어지면
        override fun onResponse(
            call: Call<List<DataClientDetail1>>,
            response: Response<List<DataClientDetail1>>
        ) {

         //   BuyerCd = keyword2
         //   GdsNo = keyword4
            dataList1 = response.body()
            val mAdapter = dataList1?.let { ExpandableAdapterHerpClient1(it, context) }
            binding.rvHerpClientlist1.adapter = mAdapter
            mAdapter?.notifyDataSetChanged()
            binding.rvHerpClientlist1.setHasFixedSize(true)
            var kks = dataList1?.size.toString()

            if(kks.equals("0")){
                noitemDisplay()

                binding.txtRstText.visibility = View.VISIBLE
                binding.svClient.clearFocus();
                binding.svClient.setQuery("", false)
            }else {
                itemDisplay()

                getClientList11(supplementService, comCd, keyword2, "${BuildConfig.API_KEY}")
            }


            //itemGetData2()

        }

    })
}



    private fun getClientList11(service: HerpclientFragment.RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.comView2(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<DataClientDetail22>> {

            override  fun onFailure(call: Call<List<DataClientDetail22>>, error: Throwable) {
                Log.d("DataClientDetail22", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataClientDetail22>>,
                response: Response<List<DataClientDetail22>>
            ) {

                val responseBody = response.body()!!

                data21.clear()
                with(data21){
                    for(itemcnt1 in responseBody) {
                      add(DataClientDetail22(itemcnt1.clientItemNo , itemcnt1.clientItemName))
                    }
                }

                val clientListData2 = DataClientDetail2(
                    false ,
                    data21
                )

                clientList2.clear()
                clientList2.add(clientListData2)
                ExpandableAdapterHerpClient2.notifyDataSetChanged()

                itemDisplay()
            }

        })
    }


}