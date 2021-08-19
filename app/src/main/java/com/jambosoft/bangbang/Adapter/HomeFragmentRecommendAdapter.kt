package com.jambosoft.bangbang.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jambosoft.bangbang.R

class HomeFragmentRecommendAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var itemList : ArrayList<String> = arrayListOf()

    init{
        itemList.add("일")
        itemList.add("이")
        itemList.add("삼")
        itemList.add("사")
        itemList.add("오")
    }

    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_frag_recommend,parent,false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView
        val textView = viewHolder.findViewById<TextView>(R.id.pageName)
        textView.text = itemList[position]

    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}