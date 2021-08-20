package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jambosoft.bangbang.PutUpRoomActivity
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.SearchContentDTO

class SearchContentAdapter(val itemList: ArrayList<SearchContentDTO>): RecyclerView.Adapter<SearchContentAdapter.ViewHolder>() {
    var context : Activity? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchContentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_content, parent, false)
        this.context = parent.context as Activity
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SearchContentAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.road.text = itemList[position].road
        holder.address.text = itemList[position].address
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            Log.e("clicked","${position}+번째 아이템")
            val intent = Intent(context,PutUpRoomActivity::class.java)
            var array : ArrayList<String> = arrayListOf()
            array.add(itemList[position].name)
            array.add(itemList[position].road)
            array.add(itemList[position].address)
            array.add(itemList[position].x.toString())
            array.add(itemList[position].y.toString())

            intent.putExtra("name",itemList[position].name)
            intent.putExtra("road",itemList[position].road)
            intent.putExtra("address",itemList[position].address)
            intent.putExtra("longitude",itemList[position].x)
            intent.putExtra("latitude",itemList[position].y)
            context?.setResult(RESULT_OK,intent)
            context?.finish()
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val road: TextView = itemView.findViewById(R.id.tv_list_road)
        val address: TextView = itemView.findViewById(R.id.tv_list_address)
    }

}