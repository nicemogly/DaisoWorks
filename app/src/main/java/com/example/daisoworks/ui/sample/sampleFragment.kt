package com.example.daisoworks.ui.sample
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.daisoworks.BuildConfig
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.R
import com.example.daisoworks.adapter.ImageAdapter2
import com.example.daisoworks.data.DataSampleDetail1
import com.example.daisoworks.data.apirstData
import com.example.daisoworks.databinding.FragmentSampleBinding
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class sampleFragment : Fragment(R.layout.fragment_sample){

    private lateinit var qrcode: EditText
    private lateinit var scanButton: ImageButton
    private lateinit var previewView: PreviewView
    private var cameraProvider: ProcessCameraProvider? = null


    //데이터 통신
    private lateinit var retrofit : Retrofit
    private lateinit var supplementService : RetrofitService

    var dataList1: List<DataSampleDetail1>? = null

    private var _binding: FragmentSampleBinding? = null
    private val binding get() = _binding!!

    private lateinit var uri: Uri
    private var uriList = ArrayList<Uri>()
    private val maxNumber = 1
    lateinit var adapter: ImageAdapter2


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSampleBinding.inflate(inflater, container, false)

        val root: View = binding.root

        return root
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceUtil(requireContext())

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)


        //RetroFit2 API 객체생성 및 Retro 서비스 객체 생생(서비스는 내부에 둠)
        retrofit = RetrofitClient.getInstance()
        supplementService = retrofit.create(RetrofitService::class.java)

        qrcode = view.findViewById(R.id.qrcode)
        scanButton = view.findViewById(R.id.scanButton)
        previewView = view.findViewById(R.id.previewView)

        scanButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ) {
                previewView.visibility = View.VISIBLE
                startCamera()
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
        }

        binding.btnRegister.setOnClickListener {
            var samplenum = qrcode.text.toString()
            var kcnt = uriList.count()
            //showAlert("샘플번호" , "${kcnt}", "확인" )

            // 1.1 샘플번호 체크
            if(samplenum.equals("")){
                showAlert("샘플번호 오류" , "QR을 스캔하여 주세요", "확인" )
            // 1.2 샘플 이미지 첨부여부
            } else if(kcnt==0 ) {
                showAlert("샘플이미지 오류" , "샘플 이미지를 선택하세요", "확인" )
            }else {

                var vcompanycode = prefs.getString("companycode", "none")

                for (i in 0 until uriList.count()) {
                    val file =
                        File(absolutelyPath(uriList.get(i), requireContext()))
                    var requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    var originfilename = "NA"+samplenum+".JPG"
                    val body =
                        MultipartBody.Part.createFormData("files", originfilename, requestFile)

                    val now: LocalDateTime = LocalDateTime.now()
                    val vdateFormat = now.format(DateTimeFormatter.ofPattern("yyyyMM"))

                    var vattr1 = samplenum
                    var vattr2 = "/IMAGES/SIN/" + vdateFormat + "/"
                    var vattr3 = "\\IMAGES\\SIN\\" + vdateFormat + "\\"
                    var vattr4 = vcompanycode
                    var vattr5 = samplenum
                    var vattr6 = "${BuildConfig.API_KEY}"


                    val content1RequestBody: RequestBody = vattr1.toPlainRequestBody()
                    val content2RequestBody: RequestBody = vattr2.toPlainRequestBody()
                    val content3RequestBody: RequestBody = vattr3.toPlainRequestBody()
                    val content4RequestBody: RequestBody = vattr4.toPlainRequestBody()
                    val content5RequestBody: RequestBody = vattr5.toPlainRequestBody()
                    val content6RequestBody: RequestBody = vattr6.toPlainRequestBody()

                    val textHashMap = hashMapOf<String, RequestBody>()
                    textHashMap["vattr1"] = content1RequestBody  //
                    textHashMap["vattr2"] = content2RequestBody  //
                    textHashMap["vattr3"] = content3RequestBody  //
                    textHashMap["vattr4"] = content4RequestBody  //
                    textHashMap["vattr5"] = content5RequestBody  //
                    textHashMap["apikey"] = content6RequestBody  // APIKEY

                    getpostImg(supplementService, body, textHashMap)


                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }


               // setUploadSample(supplementService, samplenum, "${BuildConfig.API_KEY}")
            }
        }

        binding.btnDelete.setOnClickListener {

            showConfirmDialog()

        }
    }

    private fun showConfirmDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("삭제확인")
        builder.setMessage("정말 삭제하시겠습니까?")

        builder.setPositiveButton("예") { _, _ ->

            performDelete()
        }

        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun performDelete() {
        var samplenum = qrcode.text.toString()
        if(samplenum.isNotEmpty()) {
            deleteimg(supplementService, samplenum, "${BuildConfig.API_KEY}")
        }

    }

    private fun getpostImg(service: RetrofitService, keyword1: MultipartBody.Part, keyword2: HashMap<String, RequestBody> ){
        service.postImg(keyword1,keyword2).enqueue(object: retrofit2.Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText( requireContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show()

                var vcompanycode = prefs.getString("companycode", "none")
                var samplenum = qrcode.text.toString()
                for (i in 0 until uriList.count()) {
                    val file =
                        File(absolutelyPath(uriList.get(i), requireContext()))
                    var requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    var originfilename = "NA"+samplenum+".JPG"
                    val body =
                        MultipartBody.Part.createFormData("files", originfilename, requestFile)

                    val now: LocalDateTime = LocalDateTime.now()
                    val vdateFormat = now.format(DateTimeFormatter.ofPattern("yyyyMM"))

                    var vattr1 = samplenum
                    var vattr2 = "/IMAGES/SIN/" + vdateFormat + "/"
                    var vattr3 = "\\IMAGES\\SIN\\" + vdateFormat + "\\"
                    var vattr4 = vcompanycode
                    var vattr5 = samplenum
                    var vattr6 = "${BuildConfig.API_KEY}"


                    val content1RequestBody: RequestBody = vattr1.toPlainRequestBody()
                    val content2RequestBody: RequestBody = vattr2.toPlainRequestBody()
                    val content3RequestBody: RequestBody = vattr3.toPlainRequestBody()
                    val content4RequestBody: RequestBody = vattr4.toPlainRequestBody()
                    val content5RequestBody: RequestBody = vattr5.toPlainRequestBody()
                    val content6RequestBody: RequestBody = vattr6.toPlainRequestBody()

                    val textHashMap = hashMapOf<String, RequestBody>()
                    textHashMap["vattr1"] = content1RequestBody  //
                    textHashMap["vattr2"] = content2RequestBody  //
                    textHashMap["vattr3"] = content3RequestBody  //
                    textHashMap["vattr4"] = content4RequestBody  //
                    textHashMap["vattr5"] = content5RequestBody  //
                    textHashMap["apikey"] = content6RequestBody  // APIKEY

                    getpostImg1(supplementService, body, textHashMap)


                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
               // getSampleDetail1(supplementService ,  qrcode.text.toString(), "${BuildConfig.API_KEY}")
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
              //  Toast.makeText(requireContext(), "실패했습니다.", Toast.LENGTH_SHORT).show()
               // getSampleDetail1(supplementService ,  qrcode.text.toString(), "${BuildConfig.API_KEY}")

                var vcompanycode = prefs.getString("companycode", "none")
                var samplenum = qrcode.text.toString()
                for (i in 0 until uriList.count()) {
                    val file =
                        File(absolutelyPath(uriList.get(i), requireContext()))
                    var requestFile =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    var originfilename = "NA"+samplenum+".JPG"
                    val body =
                        MultipartBody.Part.createFormData("files", originfilename, requestFile)

                    val now: LocalDateTime = LocalDateTime.now()
                    val vdateFormat = now.format(DateTimeFormatter.ofPattern("yyyyMM"))

                    var vattr1 = samplenum
                    var vattr2 = "/IMAGES/SIN/" + vdateFormat + "/"
                    var vattr3 = "\\IMAGES\\SIN\\" + vdateFormat + "\\"
                    var vattr4 = vcompanycode
                    var vattr5 = samplenum
                    var vattr6 = "${BuildConfig.API_KEY}"


                    val content1RequestBody: RequestBody = vattr1.toPlainRequestBody()
                    val content2RequestBody: RequestBody = vattr2.toPlainRequestBody()
                    val content3RequestBody: RequestBody = vattr3.toPlainRequestBody()
                    val content4RequestBody: RequestBody = vattr4.toPlainRequestBody()
                    val content5RequestBody: RequestBody = vattr5.toPlainRequestBody()
                    val content6RequestBody: RequestBody = vattr6.toPlainRequestBody()

                    val textHashMap = hashMapOf<String, RequestBody>()
                    textHashMap["vattr1"] = content1RequestBody  //
                    textHashMap["vattr2"] = content2RequestBody  //
                    textHashMap["vattr3"] = content3RequestBody  //
                    textHashMap["vattr4"] = content4RequestBody  //
                    textHashMap["vattr5"] = content5RequestBody  //
                    textHashMap["apikey"] = content6RequestBody  // APIKEY

                    getpostImg1(supplementService, body, textHashMap)


                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                }
            }
        })
    }

    private fun getpostImg1(service: RetrofitService, keyword1: MultipartBody.Part, keyword2: HashMap<String, RequestBody> ){
        service.postImg1(keyword1,keyword2).enqueue(object: retrofit2.Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText( requireContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show()
                 getSampleDetail1(supplementService ,  qrcode.text.toString(), "${BuildConfig.API_KEY}")
                 binding.btnDelete.visibility = View.VISIBLE
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                //Toast.makeText(requireContext(), "실패했습니다.", Toast.LENGTH_SHORT).show()
                 getSampleDetail1(supplementService ,  qrcode.text.toString(), "${BuildConfig.API_KEY}")
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
                        val qrCodeValue = barcodes.first().rawValue ?: ""
                        qrcode.setText(qrCodeValue) // QR 결과를 텍스트 필드에 설정
                        stopCamera() // 카메라 종료


                        getSampleDetail1(supplementService ,  qrCodeValue, "${BuildConfig.API_KEY}")

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

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 1001
        private const val TAG = "sampleFragment"
        lateinit var prefs: PreferenceUtil
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
        @GET("sampleload1")
        fun sampleView1(
            @Query("samcode") param1: String,
            @Query("apikey") param2: String
        ): Call<List<DataSampleDetail1>>

        @GET("imgdownload")
        fun imgView1(
            @Query("apikey") param1: String,
            @Query("reqno") param2: String,
            @Query("imgUrl") param3: String?
        ): Call<List<apirstData>>

        @Multipart
        @POST("sampleImg")
        fun postImg( @Part files: MultipartBody.Part , @PartMap params: Map<String, @JvmSuppressWildcards RequestBody> ): Call<String>

        @Multipart
        @POST("sampleImg1")
        fun postImg1( @Part files: MultipartBody.Part , @PartMap params: Map<String, @JvmSuppressWildcards RequestBody> ): Call<String>

        @GET("sampleImgDel")
        fun imgDel(
            @Query("sammgno") param1: String,
            @Query("apikey") param2: String
        ): Call<String>
    }

    private fun getSampleDetail1(service: RetrofitService, keyword1:String, keyword2:String){
        service.sampleView1(keyword1,keyword2).enqueue(object: retrofit2.Callback<List<DataSampleDetail1>> {

            override  fun onFailure(call: Call<List<DataSampleDetail1>>, error: Throwable) {
                Log.d("sampleView1", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<List<DataSampleDetail1>>,
                response: Response<List<DataSampleDetail1>>
            ) {
               dataList1 = response.body()
                if(dataList1?.count()!! > 0 ) {
                    binding.fl1.visibility = View.VISIBLE
                    binding.txtsamplename.text = "샘플명: " + dataList1!![0].samnm
                    binding.txtsamplecom.text = "업체명: " + dataList1!![0].clntnmkor + "(" + dataList1!![0].clntpoolno + ")"
                    binding.txtsampleyymm.text = "등록월: " + dataList1!![0].samcolym
                    //sampleimg

                    var kimg = dataList1!![0].sammgnof.trim()

                    if (kimg.isNotEmpty() ){
                        binding.sampleimg.visibility = View.VISIBLE
                        binding.fl2.visibility = View.GONE
                        binding.btnRegister.visibility = View.GONE
                        var prefixattr3 = "http://herpold.asunghmp.biz/FTP"
                        var attr5 = dataList1!![0].sammgnof +"."+dataList1!![0].filesec
                        attr5  = "NA"+attr5.trim()
                        var attr6 = dataList1!![0].vtlpath
                        var imgUrl1 = prefixattr3+attr6+attr5
                        imgUrl1 = imgUrl1.trim()
                        requestGet1(supplementService,"${BuildConfig.API_KEY}", attr5, imgUrl1)

                        Handler(Looper.getMainLooper()).postDelayed({

                            val imgUrl: String =
                                "http://59.10.47.222:3000/static/" + attr5

                            Glide.with(binding.sampleimg.context)
                                .load(imgUrl)
                                .skipMemoryCache(true) // 메모리 캐시 무시
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .error(android.R.drawable.stat_notify_error)
                                .into(binding.sampleimg)




                            // 실행 할 코드
                        }, 1000)


                        binding.btnDelete.visibility = View.VISIBLE
                    }else{

                        binding.sampleimg.visibility = View.GONE
                        binding.fl2.visibility = View.VISIBLE
                        binding.btnRegister.visibility = View.VISIBLE
                        binding.btnDelete.visibility = View.GONE

                        adapter = ImageAdapter2(requireContext(), uriList)
                        binding.recyclerview.adapter = adapter
                        // LinearLayoutManager을 사용하여 수평으로 아이템을 배치한다.
                        binding.recyclerview.layoutManager =
                            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)



                        // 클릭 이벤트 설정
                        binding.imageArea.setOnClickListener {
                           // Toast.makeText(this, "이미지를 클릭했습니다!", Toast.LENGTH_SHORT).show()
                            if (uriList.count() == maxNumber) {
                              //  showAlert("알림" , "기존 선택된 이미지는 해제됩니다..", "확인" )
                                uriList.removeAt(0)
                            }
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"

                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            registerForActivityResult.launch(intent)
                        }

                    }


                }else {
                   // Log.d("testtest" , "3")
                    binding.fl1.visibility = View.GONE
                    binding.sampleimg.visibility = View.GONE
                    binding.fl2.visibility = View.GONE
                    binding.btnRegister.visibility = View.GONE
                    binding.btnDelete.visibility = View.GONE
                }

            }

        })
    }

    private fun deleteimg(service: RetrofitService, keyword1:String, keyword2:String){
        service.imgDel(keyword1,keyword2).enqueue(object: retrofit2.Callback<String> {
            override  fun onFailure(call: Call<String>, error: Throwable) {
                Log.d("deleteimg", "실패원인: {$error}")
            }

            //Retrofit error 없이 Response 떨어지면
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                getSampleDetail1(supplementService ,  qrcode.text.toString(), "${BuildConfig.API_KEY}")
                binding.btnDelete.visibility = View.GONE
                Toast.makeText(requireContext() , "삭제되었습니다." , Toast.LENGTH_SHORT).show()
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
        val text = "${uriList.count()}/${maxNumber}"
        binding.countArea.text = text
    }



    private fun String?.toPlainRequestBody() = requireNotNull(this).toRequestBody("text/plain".toMediaTypeOrNull())
}