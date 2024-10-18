package com.example.daisoworks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.daisoworks.data.DataDmsApprove
import com.example.daisoworks.data.DataDmsDetail5
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class BottomSheetListView(context: Context, var position1 : Int  , var pitems3: MutableList<DataDmsDetail5>) :
        BottomSheetDialogFragment(){
            private lateinit var onClickListener: onDialogClickListener


    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : BottomSheetListView.RetrofitService

    private var id1 : String = ""
    companion object{
        lateinit var prefs: PreferenceUtil
    }


    override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {

        BottomSheetListView.prefs = PreferenceUtil(requireContext())

        var view:View = inflater.inflate(R.layout.custom_bottom_sheet,container)
                view.findViewById<TextView>(R.id.txtApprName).text = position1.toString()


                var VposiText = pitems3.get(position1).apprTypNm
                var VnameText = pitems3.get(position1).apprEmpNm
                var VcontText = pitems3.get(position1).apprCmmt
                var VthisText = pitems3.get(position1).myTurn
                var VreqIdText = pitems3.get(position1).reqId
                var VrevNoText = pitems3.get(position1).revNo
                var VapprSeqText = pitems3.get(position1).apprSeq
                var VapprTypText = pitems3.get(position1).apprTyp


//
//        Log.d("VposiText" , "${VposiText}")
//        Log.d("VnameText" , "${VnameText}")
//        Log.d("vapprCmmt2" , "${VcontText}")
  //      Log.d("VthisText" , "${VthisText}")
//        Log.d("VreqIdText" , "${VreqIdText}")
//        Log.d("VrevNoText" , "${VrevNoText}")
//        Log.d("VapprSeqText" , "${VapprSeqText}")
//        Log.d("VapprTypText" , "${VapprTypText}")


                id1 =  BottomSheetListView.prefs.getString("id", "none")
                //id1 = "AH1506150"
               // id1 = "AH0403070"

                view.findViewById<TextView?>(R.id.txtPosition).text = "$VposiText"
                view.findViewById<TextView?>(R.id.txtApprName).text = "$VnameText"
                view.findViewById<TextView?>(R.id.txtApprCont).text = "$VcontText"
                var TvcontText =  view.findViewById<TextView?>(R.id.txtApprCont).text


        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)





                if(VthisText == "true"){

                }else {
                    view.findViewById<TextView?>(R.id.txtApprCont).isEnabled = true
                    //view.findViewById<TextView?>(R.id.txtApprCont).setBackgroundColor(Color.LTGRAY)
                    view.findViewById<Button>(R.id.butAppr1).isEnabled = false
                    view.findViewById<Button>(R.id.butAppr2).isEnabled = false
                }


                view.findViewById<Button>(R.id.butAppr3).setOnClickListener{
                    dismiss()
                }

                view.findViewById<TextView>(R.id.lblx).setOnClickListener{
                    dismiss()
                }

                view.findViewById<Button>(R.id.butAppr1).setOnClickListener{

                    var TvcontText =  view.findViewById<TextView?>(R.id.txtApprCont).text.toString()

//                    Log.d("VreqIdText" , "${VreqIdText}" )
//                    Log.d("VrevNoText" , "${VrevNoText}" )
//                    Log.d("VapprSeqText" , "${VapprSeqText}" )
//                    Log.d("TvcontText" , "${TvcontText}" )
//                    Log.d("id1" , "${id1}" )

                    setDmsApprove(supplementService,"${BuildConfig.API_KEY}",VreqIdText, VrevNoText , VapprSeqText , VapprTypText , "Y" , TvcontText,  id1)


//                    val intent = Intent(context, SubActivity::class.java)
//                    intent.putExtra("reqNo" , "${VreqIdText}")
//                    intent.putExtra("revNo" , "${VrevNoText}")
//                    ContextCompat.startActivity(requireContext()   , intent, null)


                    dismiss()

                    (activity as SubActivity?)?.reload()
                    Toast.makeText(getActivity(), "승인 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }

                view.findViewById<Button>(R.id.butAppr2).setOnClickListener{
                    var TvcontText =  view.findViewById<TextView?>(R.id.txtApprCont).text
                    setDmsApprove(supplementService,"${BuildConfig.API_KEY}",VreqIdText, VrevNoText , VapprSeqText , VapprTypText , "N" , TvcontText.toString(), id1)
                    //  getFragmentManager()?.let { it1 -> refreshFragment(this, it1) }

                    val intent = Intent(context, SubActivity::class.java)
                    intent.putExtra("reqNo" , "${VreqIdText}")
                    intent.putExtra("revNo" , "${VrevNoText}")
                    //     intent.putExtra("reqItemDesc" , "${member.korProductDesc}")
                    ContextCompat.startActivity(requireContext()   , intent, null)
                    dismiss()
                    Toast.makeText(getActivity(), "반려 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }



                return view
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
        @GET("dmspost1")
        fun posdmsapprove(
            @Query("apikey") param1: String,
            @Query("reqId") param2: String,
            @Query("revNo") param3: String,
            @Query("apprSeq") param4: String,
            @Query("apprTyp") param5: String,
            @Query("apprCfmFg") param6: String,
            @Query("apprCmmt") param7: String,
            @Query("mUserId") param8: String
        ): Call<List<DataDmsApprove>>
    }

    private fun setDmsApprove(service: BottomSheetListView.RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String, keyword5:String, keyword6:String, keyword7:String, keyword8:String){
        service.posdmsapprove(keyword1,keyword2,keyword3,keyword4, keyword5, keyword6, keyword7, keyword8).enqueue(object: retrofit2.Callback<List<DataDmsApprove>> {

            override fun onFailure(call: Call<List<DataDmsApprove>>, error: Throwable) {
                Log.d("DataDmsApprove", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataDmsApprove>>,
                response: Response<List<DataDmsApprove>>
            ) {
                Log.d("DMS DataDmsApprove", "1")

                val responseBody = response.body()!!

                Log.d("DMS DataDmsApprove", "{$responseBody}")

                for (dmsdetail3 in responseBody) {

                }





            }

        })

    }


    // Fragment 새로고침
//    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
//        Log.d("Krefresh" , "11111111")
//        var ft: FragmentTransaction = fragmentManager.beginTransaction()
//        ft.detach(fragment).attach(fragment).commit()
//        Log.d("Krefresh" , "2222")
//    }


    fun setOnClickListener(listener: onDialogClickListener) {


            }

            interface onDialogClickListener {

                fun onClicked(clickItem: String)

            }
        }