package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.toObject
import com.jambosoft.bangbang.model.UserInfoDTO
import com.nhn.android.naverlogin.OAuthLogin

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        setProfile()

        //프로필수정 버튼
        val profileModifyButton = findViewById<Button>(R.id.user_profile_modify_btn)
        profileModifyButton.setOnClickListener {
            startActivityForResult(Intent(this,ProfileModifyActivity::class.java),200)
        }

        //로그아웃 버튼
        val logoutButton = findViewById<TextView>(R.id.user_logout_textview)
        logoutButton.setOnClickListener {
            var mOAuthInstance = OAuthLogin.getInstance()
            mOAuthInstance.logout(applicationContext)

            mOAuthInstance.logoutAndDeleteToken(applicationContext)

            Log.e("logout","클릭함")
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        //방 내놓기 버튼
        val putUpRoomButton = findViewById<TextView>(R.id.user_putup_room_textview)
        putUpRoomButton.setOnClickListener {
            startActivity(Intent(this,PutUpRoomActivity::class.java))
        }
    }

    //프로필 가져오기
    fun setProfile(){
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        db.collection("userInfo").document(user!!.uid).get().addOnSuccessListener { document ->
            if(document!=null){
                Log.e("프로필","가져오기 성공")
                var userInfoDTO = document.toObject<UserInfoDTO>()
                if(userInfoDTO!!.name.equals("")){ //이름이 null일경우
                    startActivityForResult(Intent(this,ProfileModifyActivity::class.java),200)
                }else{ //이름이 null이 아닐경우
                    val nameTextView = findViewById<TextView>(R.id.user_name)
                    val emailTextView = findViewById<TextView>(R.id.user_email)
                    val profileImageView = findViewById<ImageView>(R.id.user_profile_imageview)

                    nameTextView.text = userInfoDTO.name
                    emailTextView.text = userInfoDTO.email
                    Log.e("url user",userInfoDTO.profileUrl)
                    Glide.with(applicationContext).load(userInfoDTO.profileUrl).thumbnail(0.1f).apply(RequestOptions().centerCrop()).into(profileImageView)
                }
            }else{
                Log.e("프로필", "프로필이 없습니다.")
            }

        }.addOnFailureListener {
            Log.e("프로필","가져오기 실패")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        
        //프로필 수정
        if(resultCode==RESULT_OK&&requestCode==200){
            val user = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            var name = data?.getStringExtra("name")
            var hp = data?.getStringExtra("hp")
            db.collection("userInfo").document(user!!.uid).update("name",name)
            db.collection("userInfo").document(user!!.uid).update("hp",hp).addOnSuccessListener {
                setProfile()
            }
        }
    }
}