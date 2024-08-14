package com.example.daisoworks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daisoworks.data.ApproveItem
import com.example.daisoworks.data.Comment
import com.example.daisoworks.databinding.FragmentFirstBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentFirstBinding? = null
    private  var Ttitle : String = ""
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // RecyclerView 가 불러올 목록
    private val data1:MutableList<Comment> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.listView.setFocusable(false);
        initialize() // data 값 초기화
        //apprposition: 결재직잭  , apprid: 결재자 아이디 , apprname: 결재자 , apprdate: 결재일시 , apprflag:결재상태 , apprthis 결제현재여부
        val items3 = mutableListOf<ApproveItem>()
        items3.add(ApproveItem( "팀장","park1", "박세미", "2024.01.01 14:00" , "확인", false , "승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다승인합니다."))
        items3.add(ApproveItem( "카피팀장", "sayako" , "대상아님", "2024.01.01 14:00" , "대기" , false , ""))
        items3.add(ApproveItem( "부서장", "park1" ,  "박세미", ""  , "대기", true , ""))
        items3.add(ApproveItem( "부문장", "ahn1", "안규리", "" , "대기" , false , ""))


        val adapter = ApprListViewAdapter(this,items3)
        binding.listView.adapter = adapter
        binding.listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val item = parent.getItemAtPosition(position)
            createBottomSheet(position ,  items3 )
        }


        val adapter1 = CommentAdapterAdapter()
        adapter1.listData1 = data1
        binding.recyclerView1.adapter = adapter1
        binding.recyclerView1.layoutManager = LinearLayoutManager(context)

        //이미지 확대축소 크기 설정
        val scale = 1f
        if(scale >= binding.photoView.minimumScale && scale <= binding.photoView.maximumScale){
            binding.photoView.setScale(scale, true)
        }

        //확대축소 리스너 가능
        binding.photoView.setOnScaleChangeListener { scaleFactor , focusX , focusY ->
            if(scaleFactor > 1f) {
                //이미지가 확대되었을때 처리
            }else if (scaleFactor < 1f) {
                //이미지가 축소되었을때 처리
            }
        }

        //이미지 탭 리스너:이미지 탭했을때 위치를 리스너로 등록해서 처리할 수 있슴.
        binding.photoView.setOnScaleChangeListener { view, x, y ->
            //이미지를 탭했을때 처리
        }
/*

        binding.kkk.setOnClickListener {

            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
*/


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      // Ttitle = sharedViewModel.getData().toString()
        sharedViewModel.getData().observe(viewLifecycleOwner, Observer {
            Ttitle = it
            (activity as AppCompatActivity).supportActionBar?.setTitle(Ttitle)

        })


        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        /*   (activity as AppCompatActivity).supportActionBar?.setTitle("test")*/
        /*  binding.buttonSecond.setOnClickListener {
              findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
          }*/
    }



    private fun initialize(){
        //val reqNo: String , val reqSendName: String  , val reqRedvName: String  , val reqDate: String  , val reqConts: String
        with(data1){
            add(Comment("1042189" , "㈜아성 패션/애완/완구/목재 디자인팀 김지효(AS1610130)" , "㈜아성 주방/식사/패션/케미컬 박상연(AS1706170)" , "2022-02-04 18:17:12", "안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요  안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요  안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요  안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안  녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요안녕하세요"))
            add(Comment("1042189" , "㈜아성 패션/애완/완구/목재 디자인팀 김지효(AS1610130)" , "㈜아성 주방/식사/패션/케미컬 박상연(AS1706170)" , "2022-02-04 18:17:12", "안녕하세요"))
            add(Comment("1042189" , "㈜아성 패션/애완/완구/목재 디자인팀 김지효(AS1610130)" , "㈜아성 주방/식사/패션/케미컬 박상연(AS1706170)" , "2022-02-04 18:17:12", "안녕하세요"))

        }
    }


    private fun createBottomSheet(position1:Int,pitems3: MutableList<ApproveItem>){

        try {
                var bottomSheetListView: BottomSheetListView =
                    BottomSheetListView(requireContext(), position1 , pitems3)
                bottomSheetListView.setStyle(DialogFragment.STYLE_NORMAL, R.style.RoundCornerBottomSheetDialogTheme)


                    bottomSheetListView.show(childFragmentManager, "SET_BOTTOM_SHEET")
                    bottomSheetListView.setOnClickListener(object :
                        BottomSheetListView.onDialogClickListener {


                        override fun onClicked(clickItem: String) {

                        }
                  }
                )

            }catch (e:Exception){
                Log.d("testestest", "error")
            }
    }


    override fun onResume() {
        super.onResume()


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun EditText.setTouchForScrollBars() {
        setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }


}

