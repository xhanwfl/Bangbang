package com.jambosoft.bangbang

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.model.UserInfoDTO

class ProfileModifyActivity : AppCompatActivity() {
    var imageUri = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)


        //프로필 이미지버튼
        val profileImageView = findViewById<ImageView>(R.id.profile_modify_profile_imageview)
        profileImageView.setOnClickListener {
            setImage()
        }


        //닫기 버튼
        val closeButton = findViewById<Button>(R.id.profile_modify_close_btn)
        closeButton.setOnClickListener {
            finish()
        }

        //변경하기 버튼
        val modifyButton = findViewById<Button>(R.id.profile_modify_modify_btn)
        modifyButton.setOnClickListener {
            val nameEditText = findViewById<EditText>(R.id.profile_modify_name_edittext)
            val name = nameEditText.text.toString()
            val hpEditText = findViewById<EditText>(R.id.profile_modify_hp_edittext)
            val hp =hpEditText.text.toString()


            if(name.equals("")||hp.equals("")){ //내용을 입력하지 않을경우
                Toast.makeText(this,"내용을 모두 입력하세요",Toast.LENGTH_SHORT).show()
            }else{ //내용전부 입력할경우

                var intent = Intent(this,MainActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("hp",hp)
                intent.putExtra("uri",imageUri)


                Log.e("uri putExtra부분",imageUri)

                setResult(RESULT_OK,intent)
                finish()
            }
        }

    }

    fun setImage(){
        var intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
        startActivityForResult(intent, 500)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== RESULT_OK&&requestCode==500){
            imageUri = data?.data.toString()
            Log.e("uri2",imageUri)

            val profileImageView = findViewById<ImageView>(R.id.profile_modify_profile_imageview)
            profileImageView.setImageURI(imageUri.toUri())

        }
    }
}