package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val profileModifyButton = findViewById<Button>(R.id.user_profile_modify_btn)
        profileModifyButton.setOnClickListener {
            startActivity(Intent(this,ProfileModifyActivity::class.java))
        }
    }
}