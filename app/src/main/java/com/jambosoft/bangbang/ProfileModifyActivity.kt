package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ProfileModifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)

        val closeButton = findViewById<Button>(R.id.profile_modify_close_btn)
        closeButton.setOnClickListener {
            finish()
        }
    }
}