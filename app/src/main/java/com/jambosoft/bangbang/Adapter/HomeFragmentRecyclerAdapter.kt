package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.DetailRoomActivity
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.RoomDTO

class HomeFragmentRecyclerAdapter(val itemList : ArrayList<RoomDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var storage : FirebaseStorage
    lateinit var context : Context
    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_frag_recycler,parent,false)

        context = parent.context as Activity
        storage = FirebaseStorage.getInstance()


        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView

        //주소
        val addressTextView = viewHolder.findViewById<TextView>(R.id.item_homefrag_address_textview)
        addressTextView.text = itemList[position].address.address

        //가격
        val priceTextView = viewHolder.findViewById<TextView>(R.id.item_homfrag_price_textview)
        if(itemList[position].contractType==0){ //월세
            priceTextView.text = "월세 ${itemList[position].deposit}/${itemList[position].monthlyFee}"
        }else{
            priceTextView.text = "전세 ${itemList[position].deposit}"
        }

        //방사진
        val imageView = viewHolder.findViewById<ImageView>(R.id.item_homefrag_recycler_imageview)
        Glide.with(holder.itemView.context).load(itemList[position].images[0].toUri()).thumbnail(0.1f).apply(
            RequestOptions().centerCrop()).into(imageView)

        //이미지 클릭 이벤트
        imageView.setOnClickListener {
            var intent = Intent(context, DetailRoomActivity::class.java)
            intent.putExtra("dto",itemList[position])
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}