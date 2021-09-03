package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.jambosoft.bangbang.model.RoomInfoDTO
import kotlin.math.exp

class PutUpRoomInfoActivity : AppCompatActivity() {
    var titleEditText : EditText? = null
    var explainEditText : EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_put_up_room_info)

        //editText 초기화
        titleEditText = findViewById(R.id.putup_room_info_title)
        explainEditText = findViewById(R.id.putup_room_info_explain)

        setting()

        val applyButton = findViewById<Button>(R.id.putup_room_info_apply_btn)
        applyButton.setOnClickListener {
            getInfo()
        }
    }


    fun setting(){
        val dto = intent.getSerializableExtra("dto") as RoomInfoDTO
        titleEditText?.setText(dto.title)
        explainEditText?.setText(dto.explain)
    }

    fun getInfo() {
        val title = titleEditText?.text.toString()
        val explain = explainEditText?.text.toString()

        if(title.equals("")||explain.equals("")){ //title이나 explain이 비어있는경우
            Toast.makeText(this,"내용을 입력하세요",Toast.LENGTH_SHORT).show()
        }else{  //둘다 정상입력시
            var intent = Intent(this, PutUpRoomActivity::class.java)
            var dto = RoomInfoDTO(title, explain)
            intent.putExtra("dto",dto)

            setResult(RESULT_OK,intent)
            finish()
        }

    }
}