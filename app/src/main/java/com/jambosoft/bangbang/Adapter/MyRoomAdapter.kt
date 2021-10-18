package com.jambosoft.bangbang.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.DetailRoomActivity
import com.jambosoft.bangbang.Dialog.LoadingDialog
import com.jambosoft.bangbang.InquireListActivity
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.InquireDTO
import com.jambosoft.bangbang.model.RoomDTO
import com.jambosoft.bangbang.model.UserInfoDTO

class MyRoomAdapter(val itemList : ArrayList<RoomDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var storage : FirebaseStorage
    lateinit var context : Context
    lateinit var db : FirebaseFirestore
    lateinit var inquireList : ArrayList<InquireDTO>


    inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mycontent_room,parent,false)
        context = parent.context as Activity
        //firebaseStorage
        storage = FirebaseStorage.getInstance()
        db = FirebaseFirestore.getInstance()

        inquireList = arrayListOf()

        return CustomViewHolder(view)
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as CustomViewHolder).itemView

        //댓글
        val commentButton = viewHolder.findViewById<TextView>(R.id.item_mycontentroom_comment_textview)

        //문의
        val inquireButton = viewHolder.findViewById<TextView>(R.id.item_mycontentroom_inquire_textview)
        inquireButton.setText("문의 ${itemList[position].inquireCount}")
        inquireButton.setOnClickListener {
            var intent = Intent(context,InquireListActivity::class.java)
            intent.putExtra("timestamp",itemList[position].timestamp.toString())  //방의 timestamp를 넘김
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
        Glide.with(holder.itemView.context).load(itemList[position].images[0].toUri()).thumbnail(0.1f).apply(
            RequestOptions().centerCrop()).into(imageView)

        //이미지클릭이벤트
        imageView.setOnClickListener {
            var intent = Intent(context, DetailRoomActivity::class.java)
            intent.putExtra("dto",itemList[position])
            context.startActivity(intent)
        }

        val deleteButton = viewHolder.findViewById<Button>(R.id.item_mycontentroom_delete_btn)
        //삭제
        deleteButton.setOnClickListener {
           showDeleteDialog(position) //삭제 dialog 띄우기
        }
    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    fun showDeleteDialog(position : Int){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_content, null)
        if(dialogView.parent!=null){ //이중참조 방지
            (dialogView.parent as ViewGroup).removeView(dialogView)
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
            .setPositiveButton("확인") { dialogInterface, i ->
                val loadingDialog = LoadingDialog(context)
                loadingDialog.show()
                db.collection("rooms").document(itemList[position].timestamp.toString()).delete().addOnSuccessListener { //roomDTO 삭제
                    Toast.makeText(context,"삭제완료", Toast.LENGTH_SHORT).show()

                    db.collection("inquire").whereEqualTo("roomId",itemList[position].timestamp.toString()) // 해당 방의 inquire 모두 삭제
                        .get().addOnSuccessListener { documents ->
                            inquireList.clear()
                            for(document in documents){
                                val inquire = document.toObject(InquireDTO::class.java)
                                inquireList.add(inquire)
                                db.collection("inquire").document(inquire.timestamp.toString()).delete()
                            }
                            db.collection("userInfo").document(itemList[position].uid).get().addOnSuccessListener { //방 올린사람 alramcount 조정
                                val userInfo = it.toObject<UserInfoDTO>()
                                for(i in 0 until inquireList.size){  //inquire이 아직 체크되지 않은 수 만큼 알람 카운트를 삭제
                                    if(!inquireList[i].checked){
                                        userInfo!!.alramCount--
                                    }
                                }
                                db.collection("userInfo").document(itemList[position].uid).set(userInfo!!).addOnSuccessListener {
                                    Log.d("!MyRoomAdapter","알람카운트 조정 성공")
                                    //마지막 비동기처리 후에 item을 삭제
                                    itemList.removeAt(position)
                                    notifyItemRemoved(position)
                                    loadingDialog.dismiss()

                                    notifyDataSetChanged() //position 초기화를 위해 필요함
                                }
                            }
                        }



                    for(i in 0 until itemList[position].imageCount){ //storage에 있는 이미지 모두 삭제
                        storage.reference.child("roomImages/${itemList[position].timestamp}/${i}.jpg").delete().addOnSuccessListener {
                            Log.d("!MyRoomAdapter","storage 삭제 완료")
                        }.addOnFailureListener {
                            Log.d("!MyRoomAdapter","storage 삭제 실패 ${it}")
                        }
                    }





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