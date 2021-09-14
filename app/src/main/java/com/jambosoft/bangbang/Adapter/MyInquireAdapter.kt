package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.InquireDTO
import com.jambosoft.bangbang.model.UserInfoDTO

class MyInquireAdapter (val itemList : ArrayList<InquireDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var storage : FirebaseStorage
    lateinit var context : Context
    lateinit var db : FirebaseFirestore
    lateinit var user : FirebaseUser
    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inquire_list_inquire,parent,false)
        context = parent.context as Activity
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        //firebaseStorage
        storage = FirebaseStorage.getInstance()

        return CustomViewHolder(view)
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView

    }

    override fun getItemCount(): Int {
        return itemList.size
    }




}