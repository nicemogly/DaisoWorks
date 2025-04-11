package com.example.daisoworks.ui.exhibition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.daisoworks.ExhibitionActivity
import com.example.daisoworks.MainActivity
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.databinding.FragmentExhibitionBinding


class ExhibitionFragment : Fragment() {
    companion object{
        lateinit var prefs: PreferenceUtil
        private const val TAG = "ExhibitionFragment"
    }

    private var _binding: FragmentExhibitionBinding? = null
    var mainActivity: MainActivity? = null
    lateinit var navController: NavController

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val exhibitionViewModel =
            ViewModelProvider(this).get(ExhibitionViewModel::class.java)

        _binding = FragmentExhibitionBinding.inflate(inflater, container, false)
        val root: View = binding.root


        prefs = PreferenceUtil(requireContext())



        // 자바 스크립트 허용
        binding.webView.settings.javaScriptEnabled = true


        /* 웹뷰에서 새 창이 뜨지 않도록 방지하는 구문 */
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()

        binding.webView.addJavascriptInterface(WebAppInterface(requireContext()), "yjh")
        val parameterKey = "LoginID"
        val parameterValue =     prefs.getString("id", "none")
        //   val url = "https://example.com?${parameterKey}=${parameterValue}"

        /* 링크 주소를 로드 */
        binding.webView.loadUrl("https://ex.hanwellchina.net/appTest.aspx?LoginID=${parameterValue}")
//          binding.webView.evaluateJavascript("javascript:receiveData('JangHoon')") { result ->
//              println("JavaScript 실행 결과: $result")
//          }


        return root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    inner class WebAppInterface(private val mContext: Context) {

        @JavascriptInterface
        fun showToast(toast: String) {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()

            val intent = Intent(getActivity(), ExhibitionActivity::class.java)
            startActivity(intent)


        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
