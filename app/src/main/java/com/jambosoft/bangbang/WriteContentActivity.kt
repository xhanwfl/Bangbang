package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.jambosoft.bangbang.model.ContentDTO
import com.jambosoft.bangbang.model.UserInfoDTO

class WriteContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_content)
        val titleEditText = findViewById<EditText>(R.id.write_content_title_edittext)
        val contentEditText = findViewById<EditText>(R.id.write_content_content_edittext)



        //글쓰기버튼
        val writeButton = findViewById<Button>(R.id.write_content_write_btn)
        writeButton.setOnClickListener {
            var title = titleEditText.text.toString()
            var content = contentEditText.text.toString()
            if(title.equals("")||content.equals("")){ //제대로 입력하지 않은경우
                Toast.makeText(this,"내용을 입력하세요",Toast.LENGTH_SHORT).show()
            }else{ //제대로 입력한경우
                uploadContent(title,content)
            }
        }



    }

    fun uploadContent(title : String, content : String){
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        db.collection("userInfo").document(user!!.uid).get().addOnSuccessListener { task ->  //userid 가져오기
            if(task != null){
                var userInfo = task.toObject<UserInfoDTO>()
                if(userInfo!!.name.equals("")){
                    Log.e("프로필 가져오기","실패")
                }else{
                    var contentDTO = ContentDTO(title,content,userInfo.name,System.currentTimeMillis())

                    db.collection("contents").document().set(contentDTO).addOnSuccessListener {
                        Toast.makeText(this,"글쓰기 완료",Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }
            }
        }



    }
}