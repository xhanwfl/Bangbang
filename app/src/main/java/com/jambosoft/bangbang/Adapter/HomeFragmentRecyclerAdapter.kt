package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        priceTextView.text = "월세 ${itemList[position].deposit}/${itemList[position].monthlyFee}"

        //방사진
        val imageView = viewHolder.findViewById<ImageView>(R.id.item_homefrag_recycler_imageview)
        val ref = storage.reference.child("roomImages/${itemList[position].timestamp.toString()}")
        ref.child("0.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(holder.itemView.context).load(it).thumbnail(0.1f).apply(
                RequestOptions().centerCrop()).into(imageView)
        }.addOnFailureListener {
        }

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