package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.jambosoft.bangbang.model.ContentDTO
import com.jambosoft.bangbang.model.UserInfoDTO

class WriteContentActivity : AppCompatActivity() {
    lateinit var db : FirebaseFirestore
    lateinit var user : FirebaseUser
    lateinit var dto : ContentDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_content)
        val titleEditText = findViewById<EditText>(R.id.write_content_title_edittext)
        val contentEditText = findViewById<EditText>(R.id.write_content_content_edittext)
        db = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!

        val action = intent.getStringExtra("action")
        if(action.equals("modify")){
            dto = intent.getSerializableExtra("dto") as ContentDTO

            titleEditText.setText(dto.title)
            contentEditText.setText(dto.content)
        }else if(action.equals("write")){
            dto = ContentDTO()
        }

        //뒤로가기버튼
        val backButton = findViewById<Button>(R.id.write_content_back_btn)
        backButton.setOnClickListener {
            finish()
        }

        //글쓰기버튼
        val writeButton = findViewById<Button>(R.id.write_content_write_btn)
        writeButton.setOnClickListener{
            writeButton.isClickable = false
            if(dto.content.equals("")){ //처음 글쓰는경우
                var title = titleEditText.text.toString()
                var content = contentEditText.text.toString()
                if(title.equals("")||content.equals("")){ //제대로 입력하지 않은경우
                    Toast.makeText(this,"내용을 입력하세요",Toast.LENGTH_SHORT).show()
                }else{ //제대로 입력한경우
                    uploadContent(title,content)
                }
            }else{ //글을 수정하는경우
                dto.title = titleEditText.text.toString()
                dto.content = contentEditText.text.toString()
                if(dto.title.equals("")||dto.content.equals("")){ //제대로 입력하지 않은경우
                    Toast.makeText(this,"내용을 입력하세요",Toast.LENGTH_SHORT).show()
                }else{ //제대로 입력한경우
                    modifyContent()
                }
            }
        }
    }

    fun modifyContent(){
        db.collection("contents").document(dto.timestamp.toString()).set(dto).addOnSuccessListener {
            Toast.makeText(this,"글 수정 완료",Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun uploadContent(title : String, content : String){
        val timestamp = System.currentTimeMillis()
        db.collection("userInfo").document(user!!.uid).get().addOnSuccessListener { task ->  //userid 가져오기
            if(task != null){
                val userInfo = task.toObject<UserInfoDTO>()
                if(userInfo!!.name.equals("")){
                    Log.e("프로필 가져오기","실패")
                }else{
                    val contentDTO = ContentDTO(title,content,userInfo.name,timestamp,user!!.uid)

                    db.collection("contents").document(timestamp.toString()).set(contentDTO).addOnSuccessListener {
                        Toast.makeText(this,"글쓰기 완료",Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}