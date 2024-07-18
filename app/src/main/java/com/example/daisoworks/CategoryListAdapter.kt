package com.example.daisoworks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class CategoryListAdapter (
    private val context: Context,
    private val parentList: MutableList<String>,
    private val childList: MutableList<MutableList<String>>
): BaseExpandableListAdapter(){

    override fun getGroupCount(): Int {
        return parentList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return childList[groupPosition].size
    }

    override fun getGroup(groupPosition: Int): Any {
        return parentList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPostion: Int): Any {
        return childList[groupPosition][childPostion]
    }

    override  fun getGroupId(groupPosition: Int) : Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    //부모리스트 아이템 레이아웃 설정
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View{
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentView = inflater.inflate(R.layout.category_parent_list_item, parent,false)
        val parentCategory = parentView.findViewById<TextView>(R.id.category_parent_name)
        parentCategory.text = parentList[groupPosition]
        return parentView
    }

    //자식리스트 아이템 레이아웃 설정
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val childView = inflater.inflate(R.layout.category_child_list_item,parent,false)
        val childCategory = childView.findViewById<TextView>(R.id.category_child_name)
        childCategory.text = getChild(groupPosition, childPosition) as String
        return childView
    }

    override fun isChildSelectable(groupPosition: Int,childPosition: Int): Boolean {
        return true
    }
}