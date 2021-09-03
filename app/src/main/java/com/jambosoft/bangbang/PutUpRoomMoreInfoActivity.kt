package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.jambosoft.bangbang.model.RoomMoreInfoDTO

class PutUpRoomMoreInfoActivity : AppCompatActivity() {
    var kindsEditText : EditText? = null
    var areaEditText : EditText? = null
    var optionsEditText : EditText? = null
    var parkingEditText : EditText? = null
    var termEditText : EditText? = null
    var moveinEditText : EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_put_up_room_more_info)

        //editText 초기화
        kindsEditText = findViewById(R.id.putup_room_more_info_kinds)
        areaEditText = findViewById(R.id.putup_room_more_info_area)
        optionsEditText = findViewById(R.id.putup_room_more_info_options)
        parkingEditText = findViewById(R.id.putup_room_more_info_parking)
        termEditText = findViewById(R.id.putup_room_more_info_term)
        moveinEditText = findViewById(R.id.putup_room_more_info_movein)

        setting()

        val applyButton = findViewById<Button>(R.id.putup_room_more_info_apply_btn)
        applyButton.setOnClickListener {
            getMoreInfo()
        }
    }

    fun setting(){
       val dto = intent.getSerializableExtra("dto") as RoomMoreInfoDTO
        kindsEditText?.setText(dto.kinds)
        areaEditText?.setText(dto.area)
        optionsEditText?.setText(dto.options)
        parkingEditText?.setText(dto.parking)
        termEditText?.setText(dto.term)
        moveinEditText?.setText(dto.movein)

    }

    fun getMoreInfo(){

        val kinds = kindsEditText?.text.toString()

        val area = areaEditText?.text.toString()

        val options = optionsEditText?.text.toString()

        val parking = parkingEditText?.text.toString()

        val term = termEditText?.text.toString()

        val movein = moveinEditText?.text.toString()

        if(kinds.equals("")||area.equals("")||options.equals("")||  //하나라도 입력안했을경우
            parking.equals("")||term.equals("")||movein.equals("")){
            Toast.makeText(this,"내용을 모두 입력해주세요",Toast.LENGTH_SHORT).show()
        }else{ //모두 정상입력
            var intent = Intent(this, PutUpRoomActivity::class.java)

            var dto = RoomMoreInfoDTO(kinds,area, options, parking, term, movein)
            intent.putExtra("dto",dto)

            setResult(RESULT_OK,intent)
            finish()
        }
    }
}