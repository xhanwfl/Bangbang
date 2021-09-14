package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.content.Context
import android.util.Log
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
import com.jambosoft.bangbang.model.RoomDTO
import com.jambosoft.bangbang.model.UserInfoDTO

class InquireListAdapter(val itemList : ArrayList<InquireDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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

        //유저 아이디
        val userIdTextView = viewHolder.findViewById<TextView>(R.id.inquirelist_userid_textview)
        userIdTextView.text = itemList[position].userId

        //메세지
        val messageTextView = viewHolder.findViewById<TextView>(R.id.inquirelist_message_textview)
        messageTextView.text = itemList[position].message

        //번호 확인 버튼
        val configHpButton = viewHolder.findViewById<Button>(R.id.inquirelist_confighp_btn)
        val hpButtonString = "${itemList[position].userId}님의 연락처 확인"
        var isConfig = false
        configHpButton.setText(hpButtonString)
        configHpButton.setOnClickListener {
            if(!isConfig){
                configHpButton.setText(itemList[position].hp)
                isConfig = true
                if(!itemList[position].checked){ //아직 확인안된 알람이라면
                    db.collection("userInfo").document(user.uid).get().addOnSuccessListener {  //현재유저 info 가져와서
                        var userInfoDTO = it.toObject<UserInfoDTO>()
                        userInfoDTO!!.alramCount--
                        db.collection("userInfo").document(user.uid).set(userInfoDTO).addOnSuccessListener {  // alramCount 하나 줄이고
                            db.collection("inquire").document(itemList[position].timestamp.toString()).update("checked",true) //isChecked = true
                                .addOnSuccessListener {
                                    itemList[position].checked = true
                                    notifyDataSetChanged()
                                }
                        }
                    }
                }
            }else{
                configHpButton.setText(hpButtonString)
                isConfig = false
            }
        }

        //프로필 사진
        val profileImageView = viewHolder.findViewById<ImageView>(R.id.inquirelist_profile_imageview)
        FirebaseFirestore.getInstance().collection("userInfo").document(itemList[position].uid).get().addOnSuccessListener {
           var dto = it.toObject(UserInfoDTO::class.java)
            Glide.with(context).load(dto!!.profileUrl.toUri()).thumbnail(0.1f)
                .apply(
                    RequestOptions().centerCrop()
                ).into(profileImageView!!)
        }



    }

    override fun getItemCount(): Int {
        return itemList.size
    }




}