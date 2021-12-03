package com.jambosoft.bangbang

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.jambosoft.bangbang.model.UserInfoDTO
import java.util.concurrent.TimeUnit

class ModifyProfileActivity : AppCompatActivity() {
    var imageUri = ""
    lateinit var user : FirebaseUser
    lateinit var db : FirebaseFirestore
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    //lateinit var callbacks : PhoneAuthProvider
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)

        user = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()



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

        //이름
        val nameEditText = findViewById<EditText>(R.id.profile_modify_name_edittext)

        //이메일
        val emailEditText = findViewById<EditText>(R.id.profile_modify_email_edittext)

        db.collection("userInfo").document(user.uid).get().addOnSuccessListener { document ->
            val userInfoDTO = document.toObject<UserInfoDTO>()

            if(userInfoDTO!!.email.equals("")){
                emailEditText.visibility = View.VISIBLE

            }

        }

        //변경하기 버튼
        val modifyButton = findViewById<Button>(R.id.profile_modify_modify_btn)
        modifyButton.setOnClickListener {

            val name = nameEditText.text.toString()
            var email = ""
            if(emailEditText.isVisible){
                email = emailEditText.text.toString()
            }

            if(name.equals("")){ //이름을 입력하지 않을경우
                Toast.makeText(this,"이름을 입력하세요",Toast.LENGTH_SHORT).show()
            }else{ //내용전부 입력할경우
                var intent = Intent(this,MainActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("uri",imageUri)
                intent.putExtra("email",email)

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