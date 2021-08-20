package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.nhn.android.naverlogin.OAuthLogin

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)


        //프로필수정
        val profileModifyButton = findViewById<Button>(R.id.user_profile_modify_btn)
        profileModifyButton.setOnClickListener {
            startActivity(Intent(this,ProfileModifyActivity::class.java))
        }

        //로그아웃
        val logoutButton = findViewById<TextView>(R.id.user_logout_textview)
        logoutButton.setOnClickListener {
            var mOAuthInstance = OAuthLogin.getInstance()
            mOAuthInstance.logout(applicationContext)

            mOAuthInstance.logoutAndDeleteToken(applicationContext)

            Log.e("logout","클릭함")
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        val putUpRoomButton = findViewById<TextView>(R.id.user_putup_room_textview)
        putUpRoomButton.setOnClickListener {
            startActivity(Intent(this,PutUpRoomActivity::class.java))
        }
    }
}