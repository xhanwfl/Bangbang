package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

        //설명
        val explainTextView = viewHolder.findViewById<TextView>(R.id.item_kakaomap_explain_textview)
        explainTextView.text = itemList[position].info.explain
        // 보증금/월세
        val priceTextView = viewHolder.findViewById<TextView>(R.id.item_kakaomap_price_textview)
        priceTextView.text = "${itemList[position].deposit}/${itemList[position].monthlyFee}"
        //정보
        var roomkinds = ""
        if(itemList[position].roomKinds){
            roomkinds = "쉐어하우스"
        }else{
            roomkinds = "원룸"
        }
        val infoTextView = viewHolder.findViewById<TextView>(R.id.item_kakaomap_roominfo_textview)
        infoTextView.text = "${roomkinds} | 관리비 ${itemList[position].adminFee}만"
        //방사진
        val imageView = viewHolder.findViewById<ImageView>(R.id.item_kakaomap_room_imageview)
        val ref = storage.reference.child("roomImages/${itemList[position].timestamp.toString()}")
        ref.child("0.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(holder.itemView.context).load(it).thumbnail(0.1f).apply(
                RequestOptions().centerCrop()).into(imageView)
            Log.e("url","${ref.child("0.jpg").downloadUrl} \n ${itemList[position].images[0]}")
        }.addOnFailureListener {
            Log.e("url","왜안될까")
        }

        imageView.setOnClickListener {
            var intent = Intent(context,DetailRoomActivity::class.java)
            intent.putExtra("dto",itemList[position])
            context.startActivity(intent)
        }





    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}