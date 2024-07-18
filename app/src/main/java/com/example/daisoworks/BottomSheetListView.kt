package com.example.daisoworks

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetListView(context: Context, var position1 : Int  , var pitems3: MutableList<ApproveItem>) :
        BottomSheetDialogFragment(){
            private lateinit var onClickListener: onDialogClickListener
            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {


                var view:View = inflater.inflate(R.layout.custom_bottom_sheet,container)
                view.findViewById<TextView>(R.id.txtApprName).text = position1.toString()


                var VposiText = pitems3.get(position1).apprposition
                var VnameText = pitems3.get(position1).apprname
                var VcontText = pitems3.get(position1).apprcont
                var VthisText = pitems3.get(position1).apprthis

                view.findViewById<TextView?>(R.id.txtPosition).text = "$VposiText"
                view.findViewById<TextView?>(R.id.txtApprName).text = "$VnameText"
                view.findViewById<TextView?>(R.id.txtApprCont).text = "$VcontText"

                if(VthisText){

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
                    Toast.makeText(getActivity(), "승인 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    dismiss()
                }
                view.findViewById<Button>(R.id.butAppr2).setOnClickListener{
                    Toast.makeText(getActivity(), "반려 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    dismiss()
                }



                return view
            }



            fun setOnClickListener(listener: onDialogClickListener) {


            }

            interface onDialogClickListener {

                fun onClicked(clickItem: String)

            }
        }