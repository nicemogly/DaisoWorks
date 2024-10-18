package com.example.daisoworks.ui.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.daisoworks.LoginActivity
import com.example.daisoworks.PreferenceUtil
import com.example.daisoworks.R
import com.example.daisoworks.adapter.PagerFragmentStateAdapter
import com.example.daisoworks.fragment_tab1
import com.example.daisoworks.fragment_tab2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var callback: OnBackPressedCallback

    //내부저장소 변수 설정
    companion object{
        lateinit var prefs: PreferenceUtil
        private const val TAG = "HomeTab"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.pager)
        tabLayout = view.findViewById(R.id.tab_layout)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //내부저장소 초기화
        prefs = PreferenceUtil(requireContext())
        //내부저장소
        val LCC = prefs.getString("companycode","0")


        val pagerAdapter = PagerFragmentStateAdapter(requireActivity())
        // 6개의 fragment add
        pagerAdapter.addFragment(fragment_tab1())
        pagerAdapter.addFragment(fragment_tab2())
        viewPager.adapter = pagerAdapter

        if(LCC == "00000") {//아성다이소
            viewPager.currentItem = 1
        }else if(LCC == "10000"){ //아성에이치엠피
            viewPager.currentItem = 0
        }else if(LCC == "00001") { //아성
            viewPager.currentItem = 0
        }






        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int){
               Log.d("MainTab" , position.toString())
                if(LCC=="00000" && (position == 0 || position == null) ){

                      val builder  = AlertDialog.Builder(requireContext())
                      builder.setTitle("안내")
                          .setMessage("아성그룹 관계사 전용화면입니다.")
                          .setPositiveButton( "확인",
                              DialogInterface.OnClickListener { dialog, it ->
                                  viewPager.currentItem = 1

                              })
                          .setCancelable(false)
                    builder.show()

                }else {
                    super.onPageSelected(position)
                }
                Log.e("ViewPagerFragment", "Page ${position+1}")
            }
        })






        // tablayout attach
        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            //  tab.text = "Tab ${position+1}"
            if(position==0){
                tab.text = "HERP"
            }else if(position==1){
                tab.text = "DMS"
            }else{
                tab.text = "Tab ${position+1}"
            }

        }.attach()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //sample_text.text = "occur back pressed event!!"

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("로그아웃 안내")
                builder.setMessage("로그아웃 하시겠습니까?자동로그인이 초기화 됩니다")
                builder.setPositiveButton("확인") { dialog, which ->
                    prefs.setString("autoLoginFlagS", "0")
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                builder.setNegativeButton("취소") { dialog, which ->
                    Toast.makeText( requireContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show()

                }
                builder.show()


            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }


}