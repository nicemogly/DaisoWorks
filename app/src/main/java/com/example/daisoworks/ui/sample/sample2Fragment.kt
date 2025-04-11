package com.example.daisoworks.ui.sample

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.R
import com.example.daisoworks.adapter.ImageAdapter2
import com.example.daisoworks.data.DataSampleDetail2
import com.example.daisoworks.data.DataSampleDetail3
import com.example.daisoworks.data.apirstData
import com.example.daisoworks.databinding.FragmentSample2Binding
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class sample2Fragment : Fragment() {

    companion object {
        fun newInstance() = sample2Fragment()
        const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "sampleFragment"
        lateinit var prefs: PreferenceUtil
    }

    private lateinit var qrcode: EditText
    private lateinit var scanButton: ImageButton
    private lateinit var previewView: PreviewView


    private var cameraProvider: ProcessCameraProvider? = null

    private var _binding: FragmentSample2Binding? = null
    private val binding get() = _binding!!

    var dataList1: List<DataSampleDetail2>? = null
    var dataList2: List<DataSampleDetail3>? = null
    private var dataList2_1 = mutableListOf<String>()

    lateinit var adapter: ImageAdapter2

    private lateinit var uri: Uri
    private var uriList = ArrayList<Uri>()
    private val maxNumber = 1

    private var vrsncde: String = ""
    private var vrsnnme: String = ""

    private var SaveStatus: String = "0"
    private var SaveReason: String = "0"

    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService

    private val viewModel: Sample2ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSample2Binding.inflate(inflater, container, false)

        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sampleFragment.prefs = PreferenceUtil(requireContext())


        prefs = PreferenceUtil(requireContext())

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        qrcode = view.findViewById(R.id.qrcode)
        scanButton = view.findViewById(R.id.scanButton)
        previewView = view.findViewById(R.id.previewView)



        // TextView와 Spinner 참조
        val spinner = binding.spinnerStatus
        val spinner1 = binding.spinnerReason
        var samSave = binding.samSave

        // Spinner 데이터 설정
        val items = listOf("미채택", "채택", "보완")
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter1

        //초기화
        dataList2_1.clear()
        dataList2_1.add("사유를 선택하세요")
        SampleReason(supplementService , "${BuildConfig.API_KEY}")



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                //Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                //채택인경우 spinner1(reason) 초기화

                //Log.d("testtest" , "${selectedItem}")
                if(selectedItem.equals("채택") ) {
                    spinner1.setSelection(0)
                    spinner1.isEnabled = false
                }else if (selectedItem.equals("미채택") ) {
                    spinner1.setSelection(0)
                    spinner1.isEnabled = true
                }else{
                    spinner1.setSelection(0)
                    spinner1.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않았을 때의 처리 (필요 없을 경우 비워둘 수 있음)
            }
        }

        scanButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                previewView.visibility = View.VISIBLE
                startCamera()
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }

        samSave.setOnClickListener {
            var str1 = spinner.selectedItemPosition.toString()
            var str2 = spinner1.selectedItemPosition.toString()
            var selectedValue = spinner1.selectedItem.toString()

            val itemCode = dataList2!!.find { it.rsnnme == selectedValue }?.rsncde

            //var tmprsnposition1 = dataList2.indexOfFirst {  it == vrsnnme }
            Log.d("testteststr00" , "${itemCode}")
            if(str1.equals("2")){
                SaveStatus = "8"
            }else {
                SaveStatus = str1
            }

            if(str2.equals("0")){
                SaveReason = ""
            }else {
                SaveReason = itemCode!!
            }
             val qrcodetxt = qrcode.text.toString()

            SampleSave(supplementService , SaveStatus, SaveReason ,qrcodetxt, "${BuildConfig.API_KEY}")


        }


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


    private fun startCamera() {
        binding.sampleimg.visibility = View.GONE
        binding.fl1.visibility = View.GONE
        binding.samSave.visibility = View.GONE
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
                        val qrCodeValue = barcodes.first().rawValue ?: ""
                        qrcode.setText(qrCodeValue) // QR 결과를 텍스트 필드에 설정

                        stopCamera() // 카메라 종료
                        //Log.d("testtest" , "3")
                        getSampleDetail1(supplementService ,  qrCodeValue, "${BuildConfig.API_KEY}")
                        //Log.d("testtest" , "4")
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
        binding.sampleimg.visibility = View.VISIBLE
        binding.fl1.visibility = View.VISIBLE
        cameraProvider?.unbindAll() // 카메라 UseCase 해제
        previewView.visibility = View.GONE // PreviewView 숨기기
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == sampleFragment.CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            Log.e(TAG, "Camera permission denied")
        }
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


    interface RetrofitService {
        @GET("imgdownload")
        fun imgView1(
            @Query("apikey") param1: String,
            @Query("reqno") param2: String,
            @Query("imgUrl") param3: String?
        ): Call<List<apirstData>>

        @GET("sampleload2")
        fun sampleView2(
            @Query("samcode") param1: String,
            @Query("apikey") param2: String
        ): Call<List<DataSampleDetail2>>

        @GET("samplereason")
        fun sampleReason(
            @Query("apikey") param1: String
        ): Call<List<DataSampleDetail3>>

        @GET("samplesave")
        fun sampleSave(
            @Query("samplestatus") param1: String,
            @Query("samplereason") param2: String,
            @Query("samplecode") param3: String,
            @Query("apikey") param4: String
        ): Call<String>
    }


    private fun getSampleDetail1(service: RetrofitService, keyword1:String, keyword2:String){
        service.sampleView2(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<DataSampleDetail2>> {

            override  fun onFailure(call: Call<List<DataSampleDetail2>>, error: Throwable) {
                Log.d("sampleView1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSampleDetail2>>,
                response: Response<List<DataSampleDetail2>>
            ) {
                dataList1 = response.body()

                if(dataList1?.count()!! > 0 ) {
                    binding.fl1.visibility = View.VISIBLE
                    binding.txtsampleno.text = "샘플번호: NA" + dataList1!![0].sammgno
                    binding.txtsamplename.text = "샘 플 명: " + dataList1!![0].samnm
                    binding.txtsampledeptnm.text = "담당부서: " + dataList1!![0].deptsnme
                    binding.txtsampledepttm.text = "담 당 팀: " + dataList1!![0].deptnme


                    binding.samSave.visibility = View.VISIBLE

                    var kimg = dataList1!![0].vtlpath
                    if(kimg.isNullOrEmpty()) {
                        binding.sampleimg.visibility = View.GONE
                        binding.fl2.visibility = View.VISIBLE
                        adapter = ImageAdapter2(requireContext(), uriList)
                        //   binding.recyclerview.adapter = adapter
                        // LinearLayoutManager을 사용하여 수평으로 아이템을 배치한다.
                        // binding.recyclerview.layoutManager =
                        //   LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    } else {
                        binding.sampleimg.visibility = View.VISIBLE
                        binding.fl2.visibility = View.GONE
                        //  binding.btnRegister.visibility = View.GONE
                        var prefixattr3 = "http://herpold.asunghmp.biz/FTP"
                        var attr5 = dataList1!![0].sammgno +".JPG"
                        attr5  = "NA"+attr5.trim()
                        var attr6 = dataList1!![0].vtlpath
                        var imgUrl1 = prefixattr3+attr6+attr5
                        imgUrl1 = imgUrl1.trim()

                        if (dataList1!![0].adpgbn.isNullOrEmpty()) {
                            binding.spinnerStatus.setSelection(0)
                            binding.spinnerReason.isEnabled = true
                        }else if (dataList1!![0].adpgbn.equals("0")){
                                binding.spinnerStatus.setSelection(0)
                            binding.spinnerReason.isEnabled = true
                        }else if (dataList1!![0].adpgbn.equals("1")){
                            binding.spinnerStatus.setSelection(1)
                            binding.spinnerReason.setSelection(0)
                            binding.spinnerReason.isEnabled = false
                        }else if  (dataList1!![0].adpgbn.equals("8")) {
                            binding.spinnerStatus.setSelection(2)
                            binding.spinnerReason.setSelection(0)
                            binding.spinnerReason.isEnabled = false
                        }
                        var aaaa = dataList1!![0].rsncde
                        Log.d("testtest0" , "${aaaa}")
                        if(dataList1!![0].rsncde.isNullOrEmpty()){
                         //   dataList2_1.add("미채택사유 선택")
                        }else{
                             vrsncde  = dataList1!![0].rsncde
                             vrsnnme  = dataList1!![0].rsnnme
                            Log.d("testtest1" , "${vrsnnme}")

                             //  vrsnnme = "단가오류"
                             var tmprsnposition = dataList2_1.indexOfFirst {  it == vrsnnme }
                             Log.d("testtest2" , "${tmprsnposition}")

                            if (tmprsnposition != -1) {
                                binding.spinnerReason.setSelection(tmprsnposition)
                            } else {
                                binding.spinnerReason.setSelection(0)
                            }



                       }


                        val progressBar = binding.progressBar

                        // 로딩 중에 ProgressBar 표시
                        progressBar.visibility = View.VISIBLE

                        requestGet1(supplementService,"${BuildConfig.API_KEY}", attr5, imgUrl1)
                        Handler(Looper.getMainLooper()).postDelayed({

                            val imgUrl: String =
                                "http://59.10.47.222:3000/static/" + attr5

                            Glide.with(binding.sampleimg.context)
                                .load(imgUrl)

                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        // 로드 실패 시 ProgressBar 숨김
                                        progressBar.visibility = View.GONE
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        // 로드 성공 시 ProgressBar 숨김
                                        progressBar.visibility = View.GONE
                                        return false
                                    }
                                })
                                .skipMemoryCache(true) // 메모리 캐시 무시
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .error(android.R.drawable.stat_notify_error)
                                .into(binding.sampleimg)


                            // 로드 완료 시 ProgressBar 숨김
                            // 실행 할 코드
                        }, 500)
                        //  binding.btnDelete.visibility = View.VISIBLE
                        //progressBar.visibility = View.GONE
                    }



                }else {
                    // Log.d("testtest" , "3")
                    binding.fl1.visibility = View.GONE
                    binding.sampleimg.visibility = View.GONE
                    binding.fl2.visibility = View.GONE
               //    binding.btnRegister.visibility = View.GONE
               //     binding.btnDelete.visibility = View.GONE
                }

            }

        })
    }


    private fun requestGet1(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String){
        service.imgView1(keyword1,keyword2,keyword3).enqueue(object: retrofit2.Callback<List<apirstData>> {


            override  fun onFailure(call: Call<List<apirstData>>, error: Throwable) {
                Log.d("apirstData", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<apirstData>>,
                response: Response<List<apirstData>>
            ) {

                Log.d("apirstData", "ok")
            }

        })
    }



    private fun SampleSave(service: RetrofitService, keyword1:String, keyword2:String, keyword3:String, keyword4:String){
        service.sampleSave(keyword1,keyword2,keyword3,keyword4).enqueue(object: retrofit2.Callback<String> {


            override  fun onFailure(call: Call<String>, error: Throwable) {
                Log.d("DataSampleDetail3", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if(response.body()=="ok") {
                //  Log.d("testtest" , "okay")
                    getSampleDetail1(supplementService ,  keyword3, "${BuildConfig.API_KEY}")
                }
            }

        })
    }


    private fun SampleReason(service: RetrofitService, keyword1:String){
        service.sampleReason(keyword1).enqueue(object: retrofit2.Callback<List<DataSampleDetail3>> {


            override  fun onFailure(call: Call<List<DataSampleDetail3>>, error: Throwable) {
                Log.d("DataSampleDetail3", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSampleDetail3>>,
                response: Response<List<DataSampleDetail3>>
            ) {
                // TextView와 Spinner 참조
                val spinner1 = binding.spinnerReason
                dataList2 = response.body()
               // val codes = response.body() ?: emptyList()
                val dataList2cnt = dataList2!!.count()

                for (i in 0 until dataList2cnt) {
                    dataList2_1.add(dataList2!!.get(i).rsnnme)
                }

                val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dataList2_1)
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner1.adapter = adapter2

                Log.d("DataSampleDetail3", "ok")
            }

        })
    }



    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    val clipData = result.data?.clipData
                    if (clipData != null) { // 이미지를 여러 개 선택할 경우
                        val clipDataSize = clipData.itemCount
                        val selectableCount = maxNumber - uriList.count()
                        if (clipDataSize > selectableCount) { // 최대 선택 가능한 개수를 초과해서 선택한 경우
                            showAlert("알림" , "최대 선택 가능한 이미지는 1개입니다.", "확인" )
                        } else {
                            // 선택 가능한 경우 ArrayList에 가져온 uri를 넣어준다.
                            for (i in 0 until clipDataSize) {
                                uriList.add(clipData.getItemAt(i).uri)
                            }
                        }
                    } else { // 이미지를 한 개만 선택할 경우 null이 올 수 있다.
                        val uri = result?.data?.data
                        if (uri != null) {
                            uriList.add(uri)
                        }
                    }
                    // notifyDataSetChanged()를 호출하여 adapter에게 값이 변경 되었음을 알려준다.
                    adapter.notifyDataSetChanged()
                    printCount()
                }
            }
        }


    // 절대경로 변환
    fun absolutelyPath(path: Uri?, context : Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)

        return result!!
    }


    private fun printCount() {
     //   val text = "${uriList.count()}/${maxNumber}"
     //   binding.countArea.text = text
    }



    private fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())

}