package com.example.daisoworks.ui.sample

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.R
import com.example.daisoworks.adapter.ItemAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
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

class BottomSheetFragment(private val listener: BottomSheetDismissListener) : BottomSheetDialogFragment() {


    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService


    private lateinit var scanButton: Button
    private lateinit var scanButton1: Button
    private lateinit var btnRegist: Button
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var previewView: PreviewView
    private lateinit var itemAdapter: ItemAdapter
    private var errsamAccept: Int  = 0
   // private var previtem: String = ""

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "sample1Fragment"
        lateinit var prefs: PreferenceUtil
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        prefs = PreferenceUtil(requireContext())

        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)


        val view = inflater.inflate(R.layout.bottom_sheet_layout1, container, false)
        scanButton = view.findViewById(R.id.scanButton)
        scanButton1 = view.findViewById(R.id.scanButton1)
        previewView = view.findViewById(R.id.previewView)
        btnRegist = view.findViewById(R.id.btnRegist)


        // 스캔 버튼 클릭 이벤트
        scanButton.setOnClickListener {
//            (activity as? MainActivity)?.startBarcodeScanning()
//            dismiss() // BottomSheet 닫기
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                previewView.visibility = View.VISIBLE
                startCamera()
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    sampleFragment.CAMERA_PERMISSION_REQUEST_CODE
                )
            }

        }

        scanButton1.setOnClickListener {
            stopCamera() // 카메라 종료
        }



        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewSample)
       //val addButton = findViewById<Button>(R.id.addButton)

        // 초기 리스트 데이터
        //  val initialList = mutableListOf("스캔된 항목")
        //  itemAdapter = ItemAdapter(initialList)
        //  itemAdapter = ItemAdapter(mutableListOf())

        itemAdapter = ItemAdapter(mutableListOf()) { barcode ->
            itemAdapter.deleteBarcode(barcode.toString())
            Toast.makeText(requireContext(), "삭제됨: $barcode", Toast.LENGTH_SHORT).show()
        }
        // RecyclerView 설정
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = itemAdapter


        btnRegist.setOnClickListener {

           stopCamera()
           dismiss()



        }



        return view
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onBottomSheetDismissed() // BottomSheet가 닫힐 때 인터페이스 호출
    }

    interface BottomSheetDismissListener {
        fun onBottomSheetDismissed()
    }

    private fun getSamAccept(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.samAccept(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<String> {
            override  fun onFailure(call: Call<String>, error: Throwable) {
                Log.d("샘플접수등록 접속 실패", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
//                getSampleDetail1(supplementService ,  qrcode.text.toString(), "${BuildConfig.API_KEY}")
//                binding.btnDelete.visibility = View.GONE
               // Toast.makeText(requireContext() , "삭제되었습니다." , Toast.LENGTH_SHORT).show()
               //println("testtest11"+response.body())
              //  errsamAccept

                //Log.d("testtest11" , "aaaa:${response.body()}")
                //    showAlert("알림", "샘플추가 완료($scannedValue)", "확인")
//                if(response.body()=="fail"){
//                    showAlert("알림", "샘플접수 완료처리중 오류:" + keyword2, "확인")
//                }
                if(response.body()=="ok") {
                    itemAdapter.addItem(keyword2)
                    // previtem = scannedValue
//                                    Toast.makeText(
//                                        requireContext(),
//                                        "샘플추가 완료: $scannedValue",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                    showAlert("알림", "샘플접수 완료($keyword2)", "확인")
                    stopCamera()

                    // errsamAccept = errsamAccept + 1
                }else{
                    showAlert("알림", "이미 샘플접수 등록이 되었거나 유효하지 않은 샘플코드입니다.($keyword2)", "확인")
                    stopCamera()
                }
            }
        })
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                processQRCode(imageProxy)
            }

            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind use cases", e)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processQRCode(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {

                        //Log.d("testtest" , "${itemAdapter.itemCount}")
                        var memempmgnum = prefs.getString("memempmgnum", "none")
                        //Log.d("testtest" , "memempmgnum:${memempmgnum}")


                        val qrCodeValue = barcodes.first().rawValue ?: ""
                        for (barcode in barcodes) {
                            val scannedValue = barcode.rawValue

//                            if (previtem.equals(scannedValue)) {
//                                showAlert("알림", "이미 스캔된 샘플:($scannedValue)", "확인")
//                                stopCamera()
//                            }else{
                                // 중복 확인
                                if (scannedValue != null) {
                                    if (itemAdapter.isDuplicate(scannedValue)) {
//                                    Toast.makeText(
//                                        requireContext(),
//                                        "이미 스캔된 샘플코드: $scannedValue",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                        showAlert("알림", "이미 스캔된 샘플:($scannedValue)", "확인")
                                        stopCamera()

                                    } else {

                                        getSamAccept(supplementService, memempmgnum, scannedValue,"${BuildConfig.API_KEY}")
                                        stopCamera()
                                    }
                                    btnRegist.visibility = View.VISIBLE
                                }
                           // }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "QR Code scan failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }




    private fun stopCamera() {
        cameraProvider?.unbindAll() // 카메라 UseCase 해제
        previewView.visibility = View.GONE // PreviewView 숨기기
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            Log.e(TAG, "Camera permission denied")
        }
    }


    fun showAlert(str1: String ,str2: String , str3: String ){

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(str1)
        builder.setMessage(str2)
        builder.setPositiveButton(str3, DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(requireContext(), str3, Toast.LENGTH_SHORT).show()
              lifecycleScope.launch {
                  delay(1000)
                  if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                      == PackageManager.PERMISSION_GRANTED
                  ) {
                      previewView.visibility = View.VISIBLE
                      startCamera()
                  } else {
                      requestPermissions(arrayOf(Manifest.permission.CAMERA),
                          sampleFragment.CAMERA_PERMISSION_REQUEST_CODE
                      )
                  }

              }
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
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
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
        @GET("sampleaccept")
        fun samAccept(
            @Query("memempmgnum") param1: String,
            @Query("barcode") param2: String,
            @Query("apikey") param3: String
        ): Call<String>
    }

    }
