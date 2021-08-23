package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class PutUpRoomMoreInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_put_up_room_more_info)

        val applyButton = findViewById<Button>(R.id.putup_room_more_info_apply_btn)
        applyButton.setOnClickListener {
            getMoreInfo()
        }
    }

    fun getMoreInfo(){
        val kindsEditText = findViewById<EditText>(R.id.putup_room_more_info_kinds)
        val kinds = kindsEditText.text.toString()

        val areaEditText = findViewById<EditText>(R.id.putup_room_more_info_area)
        val area = areaEditText.text.toString()

        val optionsEditText = findViewById<EditText>(R.id.putup_room_more_info_options)
        val options = optionsEditText.text.toString()

        val parkingEditText = findViewById<EditText>(R.id.putup_room_more_info_parking)
        val parking = parkingEditText.text.toString()

        val termEditText = findViewById<EditText>(R.id.putup_room_more_info_term)
        val term = termEditText.text.toString()

        val moveinEditText = findViewById<EditText>(R.id.putup_room_more_info_movein)
        val movein = moveinEditText.text.toString()

        if(kinds.equals("")||area.equals("")||options.equals("")||  //하나라도 입력안했을경우
            parking.equals("")||term.equals("")||movein.equals("")){
            Toast.makeText(this,"내용을 모두 입력해주세요",Toast.LENGTH_SHORT).show()
        }else{ //모두 정상입력
            var intent = Intent(this, PutUpRoomActivity::class.java)
            intent.putExtra("kinds",kinds)
            intent.putExtra("area",area)
            intent.putExtra("options",options)
            intent.putExtra("parking",parking)
            intent.putExtra("term",term)
            intent.putExtra("movein",movein)

            setResult(RESULT_OK,intent)
            finish()
        }
    }
}