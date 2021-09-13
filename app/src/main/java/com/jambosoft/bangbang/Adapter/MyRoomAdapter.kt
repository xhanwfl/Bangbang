package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.DetailRoomActivity
import com.jambosoft.bangbang.InquireListActivity
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.RoomDTO

class MyRoomAdapter(val itemList : ArrayList<RoomDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var storage : FirebaseStorage
    lateinit var context : Context
    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)
    lateinit var dialogView : View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mycontent_room,parent,false)
        context = parent.context as Activity
        dialogView = LayoutInflater.from(parent.context).inflate(R.layout.dialog_delete_content, null)
        //firebaseStorage
        storage = FirebaseStorage.getInstance()

        return CustomViewHolder(view)
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView

        //댓글
        val commentButton = viewHolder.findViewById<Button>(R.id.item_mycontentroom_comment_btn)

        //문의
        val inquireButton = viewHolder.findViewById<Button>(R.id.item_mycontentroom_inquire_btn)
        inquireButton.setText("문의 ${itemList[position].inquireCount}")
        inquireButton.setOnClickListener {
            var intent = Intent(context,InquireListActivity::class.java)
            intent.putExtra("timestamp",itemList[position].timestamp.toString())
            context.startActivity(intent)

            //알람..관련....

        }

        // 보증금/월세
        val priceTextView = viewHolder.findViewById<TextView>(R.id.item_mycontentroom_price_textview)
        priceTextView.text = "${itemList[position].deposit}/${itemList[position].monthlyFee}"

        //정보
        val infoTextView = viewHolder.findViewById<TextView>(R.id.item_mycontentroom_roominfo_textview)
        infoTextView.text = "${itemList[position].info.title}"

        //방사진
        val imageView = viewHolder.findViewById<ImageView>(R.id.item_mycontentroom_room_imageview)
        val ref = storage.reference.child("roomImages/${itemList[position].timestamp}")
        ref.child("0.jpg").downloadUrl.addOnSuccessListener {
            Glide.with(holder.itemView.context).load(it).thumbnail(0.1f).apply(
                RequestOptions().centerCrop()).into(imageView)
        }.addOnFailureListener {

        }

        //이미지클릭이벤트
        imageView.setOnClickListener {
            var intent = Intent(context, DetailRoomActivity::class.java)
            intent.putExtra("dto",itemList[position])
            context.startActivity(intent)
        }

        val deleteButton = viewHolder.findViewById<Button>(R.id.item_mycontentroom_delete_btn)
        //삭제
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    FirebaseFirestore.getInstance().collection("rooms").document(itemList[position].timestamp.toString()).delete().addOnSuccessListener {
                        Toast.makeText(context,"삭제완료", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }.addOnFailureListener {
                        Toast.makeText(context,"삭제실패", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                }
                .show()
        }
    }
    override fun getItemCount(): Int {
        return itemList.size
    }
}