package com.jambosoft.bangbang.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.jambosoft.bangbang.DetailViewActivity
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.ContentDTO
import java.text.SimpleDateFormat

class CommunityFragmentRecentAdapter(itemList : ArrayList<ContentDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList : ArrayList<ContentDTO>? = null
    var context : Context? = null
    init{
        this.itemList = itemList
    }

    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comunity_frag_content,parent,false)
        context = view.context

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView

        val titleTextView = viewHolder.findViewById<TextView>(R.id.item_content_recycler_title)
        val userIdTextView = viewHolder.findViewById<TextView>(R.id.item_content_recycler_userid)
        val timestampTextView = viewHolder.findViewById<TextView>(R.id.item_content_recycler_timestamp)
        val itemView = viewHolder.findViewById<ConstraintLayout>(R.id.item_content_recycler_itemview)

        titleTextView.text = itemList!![position].title
        userIdTextView.text = itemList!![position].userId

        val sdf = SimpleDateFormat("yyyy-MM-dd-")
        val date = sdf.format(itemList!![position].timestamp)
        timestampTextView.text = date

        itemView.setOnClickListener {
            var intent = Intent(context,DetailViewActivity::class.java)
            intent.putExtra("timestamp",itemList!![position].timestamp)
            context!!.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return itemList!!.size
    }


}