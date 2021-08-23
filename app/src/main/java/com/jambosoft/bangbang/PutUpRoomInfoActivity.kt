package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlin.math.exp

class PutUpRoomInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_put_up_room_info)

        val applyButton = findViewById<Button>(R.id.putup_room_info_apply_btn)
        applyButton.setOnClickListener {
            getInfo()
        }
    }

    fun getInfo() {


        val titleEditText = findViewById<EditText>(R.id.putup_room_info_title)
        val title = titleEditText.text.toString()

        val explainEditText = findViewById<EditText>(R.id.putup_room_info_explain)
        val explain = explainEditText.text.toString()



        if(title.equals("")||explain.equals("")){ //title이나 explain이 비어있는경우
            Toast.makeText(this,"내용을 입력하세요",Toast.LENGTH_SHORT).show()
        }else{  //둘다 정상입력시
            var intent = Intent(this, PutUpRoomActivity::class.java)
            intent.putExtra("title",title)
            intent.putExtra("explain", explain)

            setResult(RESULT_OK,intent)
            finish()
        }

    }
}