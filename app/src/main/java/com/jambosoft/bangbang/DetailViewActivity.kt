package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jambosoft.bangbang.model.ContentDTO
import com.jambosoft.bangbang.model.UserInfoDTO
import java.text.SimpleDateFormat

class DetailViewActivity : AppCompatActivity() {
    var dto = ContentDTO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)
        val user = FirebaseAuth.getInstance().currentUser


        //backbutton
        val backButton = findViewById<Button>(R.id.detailview_back_btn)
        backButton.setOnClickListener {
            finish()
        }


        //내용물 세팅
        val commentRecyclerView = findViewById<RecyclerView>(R.id.detailview_comment_recycler)
        val titleTextView = findViewById<TextView>(R.id.detailview_title_textview)
        val contentTextView = findViewById<TextView>(R.id.detailview_content_textview)
        val userIdTextView = findViewById<TextView>(R.id.detailview_userid_textview)
        val timestampTextView = findViewById<TextView>(R.id.detailview_timestamp_textview)
        val recommandTextView = findViewById<TextView>(R.id.detailview_recommand_textview)
        var timestamp = intent.getLongExtra("timestamp",0)
        val modifyTextView = findViewById<TextView>(R.id.detailview_modify_textview)
        val deleteTextView = findViewById<TextView>(R.id.detailview_delete_textview)
        Log.e("timestamp"," : ${timestamp}")

        val db = FirebaseFirestore.getInstance()
        db.collection("contents").whereEqualTo("timestamp",timestamp).get().addOnSuccessListener { task ->

            for(document in task){
                dto = document.toObject(ContentDTO::class.java)
            }

            titleTextView.text = dto.title
            contentTextView.text = dto.content
            userIdTextView.text = dto.userId
            var sdf = SimpleDateFormat("yyyy-MM-dd")
            var time = sdf.format(timestamp)
            timestampTextView.text = time
            recommandTextView.text = dto.favoriteCount.toString()
            if(dto.uid.equals(user!!.uid)){
                modifyTextView.visibility = View.VISIBLE
                deleteTextView.visibility = View.VISIBLE
            }

            commentRecyclerView.layoutManager = LinearLayoutManager(this)
            commentRecyclerView.adapter = DetailViewCommentAdapter()
        }



        //수정
        modifyTextView.setOnClickListener {
            var intent = Intent(this,WriteContentActivity::class.java)
            intent.putExtra("dto",dto)
            intent.putExtra("action","modify")
            startActivity(intent)
            finish()
        }


        //삭제
        deleteTextView.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_delete_content, null)

            builder.setView(dialogView)
                .setPositiveButton("확인") { dialogInterface, i ->
                    db.collection("contents").document(dto.timestamp.toString()).delete().addOnSuccessListener {
                        Toast.makeText(this,"삭제완료",Toast.LENGTH_SHORT).show()

                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this,"삭제실패",Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /* 취소일 때 아무 액션이 없으므로 빈칸 */
                }
                .show()
        }




        //추천
        val recommandButton = findViewById<Button>(R.id.detailview_recommand_btn)
        recommandButton.setOnClickListener {
            if(dto.favorites.get(user!!.uid)==null||dto.favorites.get(user!!.uid)==false){ //추천을 한번누를경우
                dto.favorites.set(user!!.uid,true)
                dto.favoriteCount++
                db.collection("contents").document(dto.timestamp.toString()).set(dto)
                recommandTextView.text = dto.favoriteCount.toString()
            }else{
                dto.favorites.set(user!!.uid,false)
                dto.favoriteCount--
                db.collection("contents").document(dto.timestamp.toString()).set(dto)
                recommandTextView.text = dto.favoriteCount.toString()
            }
        }

        //댓글
       /* val commentRecyclerView = findViewById<RecyclerView>(R.id.detailview_comment_recycler)
        commentRecyclerView.layoutManager = LinearLayoutManager(this)
        commentRecyclerView.adapter = DetailViewCommentAdapter(dto.)*/

        val commentEditText = findViewById<EditText>(R.id.detailview_comment_edittext)
        val commentButton = findViewById<Button>(R.id.detailview_comment_button)

        //댓글 버튼
        commentButton.setOnClickListener {
            var comment = commentEditText.text.toString()
            if(comment.equals("")){ //내용 미입력시
                Toast.makeText(this,"내용을 입력해주세요",Toast.LENGTH_SHORT).show()
            }else{  //모두입력시
                var userInfoDTO = UserInfoDTO()
                var timestamp = System.currentTimeMillis()

                //유저정보가져와서
                db.collection("userInfo").whereEqualTo("email",user!!.email).get().addOnSuccessListener {
                    for(document in it){
                        userInfoDTO = document.toObject(UserInfoDTO::class.java)
                    }

                    //firestore에 코멘트 올리기
                    var commentDTO = ContentDTO.Comment(user!!.uid,userInfoDTO.name,comment,timestamp)
                    db.collection("contents").document(dto.timestamp.toString()).collection("comments").document()
                        .set(commentDTO)

                    commentEditText.setText("")
                    }
                }
            }




        }


    //recyclerview adapter
    inner class DetailViewCommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val comments : ArrayList<ContentDTO.Comment>
        init{
            comments = ArrayList()
            var commentSnapshot = FirebaseFirestore
                .getInstance()
                .collection("contents")
                .document(dto.timestamp.toString())
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot == null) return@addSnapshotListener
                    for(snapshot in querySnapshot?.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }

        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detailview_comment,parent,false)

            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder = (holder as CustomViewHolder).itemView
            val userIdTextView = viewHolder.findViewById<TextView>(R.id.item_detailview_comment_userid)
            userIdTextView.text = comments[position].userId
            val commentTextView = viewHolder.findViewById<TextView>(R.id.item_detailview_comment_comment)
            commentTextView.text = comments[position].comment

        }

        override fun getItemCount(): Int {
            return comments.size
        }


    }



    }
