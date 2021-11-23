package com.jambosoft.bangbang.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jambosoft.bangbang.DetailRoomActivity
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.RoomDTO
import java.net.URL
import java.net.URLEncoder
import kotlin.coroutines.coroutineContext

class KakaoMapAdapter(val itemList : ArrayList<RoomDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var storage : FirebaseStorage
    lateinit var context : Context
    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kakaomap_rooms,parent,false)
        context = parent.context as Activity

        //firebaseStorage
        storage = FirebaseStorage.getInstance()

        return CustomViewHolder(view)
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView

        val itemLayout = viewHolder.findViewById<ConstraintLayout>(R.id.item_kakaomap_item_layout)


        //설명
        val titleTextView = viewHolder.findViewById<TextView>(R.id.item_kakaomap_explain_textview)
        titleTextView.text = itemList[position].info.title
        // 보증금
        val priceTextView = viewHolder.findViewById<TextView>(R.id.item_kakaomap_price_textview)
        if(itemList[position].contractType==0){ //월세
            priceTextView.text = "월세 ${itemList[position].deposit}/${itemList[position].monthlyFee}"
        }else{
            priceTextView.text = "전세 ${itemList[position].deposit}"
        }

        //정보
        var roomkinds = ""
        when(itemList[position].roomKinds){
            0 ->{ roomkinds = "원룸" }
            1 ->{ roomkinds = "투·쓰리룸" }
            2 ->{ roomkinds = "오피스텔" }
            3 ->{ roomkinds = "쉐어하우스" }
        }
        val infoTextView = viewHolder.findViewById<TextView>(R.id.item_kakaomap_roominfo_textview)
        infoTextView.text = "${roomkinds} | 관리비 ${itemList[position].adminFee}만"
        //방사진
        val imageView = viewHolder.findViewById<ImageView>(R.id.item_kakaomap_room_imageview)
        Glide.with(holder.itemView.context).load(itemList[position].images[0].toUri()).thumbnail(0.1f).apply(
            RequestOptions().centerCrop()).into(imageView)

        imageView.setOnClickListener {
            openDetailRoom(position)
        }
        itemLayout.setOnClickListener {
            openDetailRoom(position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    fun openDetailRoom(position : Int){
        var intent = Intent(context,DetailRoomActivity::class.java)
        intent.putExtra("dto",itemList[position])
        context.startActivity(intent)
    }

}