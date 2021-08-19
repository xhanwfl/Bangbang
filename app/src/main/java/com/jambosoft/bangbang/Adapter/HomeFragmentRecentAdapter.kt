package com.jambosoft.bangbang.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jambosoft.bangbang.R

class HomeFragmentRecentAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var itemList : ArrayList<String> = arrayListOf()

    init{
        itemList.add("1")
        itemList.add("2")
        itemList.add("3")
        itemList.add("4")
        itemList.add("5")
    }

    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_frag_recent,parent,false)

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