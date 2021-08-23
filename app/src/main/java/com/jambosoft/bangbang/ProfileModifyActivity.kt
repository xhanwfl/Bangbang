package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ProfileModifyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modify)


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

            if(name.equals("")||hp.equals("")){ //내용을 전부 입력하지 않을경우
                Toast.makeText(this,"내용을 모두 입력하세요",Toast.LENGTH_SHORT).show()
            }else{ //모두 입력할경우
                var intent = Intent(this,UserActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("hp",hp)
                setResult(RESULT_OK,intent)
                finish()
            }
        }

    }
}