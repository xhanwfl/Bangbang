package com.jambosoft.bangbang

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
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
        areaEditText = findViewById(R.id.putup_room_more_info_area)
        optionsEditText = findViewById(R.id.putup_room_more_info_options)
        parkingEditText = findViewById(R.id.putup_room_more_info_parking)
        termEditText = findViewById(R.id.putup_room_more_info_term)
        moveinEditText = findViewById(R.id.putup_room_more_info_movein)

        setting()

        //뒤로가기버튼
        val backButton = findViewById<Button>(R.id.putup_room_more_info_back_btn)
        backButton.setOnClickListener {
            finish()
        }


        //적용하기버튼
        val applyButton = findViewById<Button>(R.id.putup_room_more_info_apply_btn)
        applyButton.setOnClickListener {
            getMoreInfo()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView : View? = currentFocus
        val rect = Rect()
        focusView?.getGlobalVisibleRect(rect)
        val x = ev?.x?.toInt()
        val y = ev?.y?.toInt()
        if(!rect.contains(x!!,y!!)){
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(focusView?.windowToken,0)
            focusView?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    fun setting(){
       val dto = intent.getSerializableExtra("dto") as RoomMoreInfoDTO
        if(dto.area!=0){
            areaEditText?.setText(dto.area.toString())
        }
        optionsEditText?.setText(dto.options)
        parkingEditText?.setText(dto.parking)
        termEditText?.setText(dto.term)
        moveinEditText?.setText(dto.movein)
    }

    fun getMoreInfo(){
        val area = areaEditText?.text.toString().toInt()
        val options = optionsEditText?.text.toString()
        val parking = parkingEditText?.text.toString()
        val term = termEditText?.text.toString()
        val movein = moveinEditText?.text.toString()

        if(area==0||options.equals("")||  //하나라도 입력안했을경우
            parking.equals("")||term.equals("")||movein.equals("")){
            Toast.makeText(this,"내용을 모두 입력해주세요",Toast.LENGTH_SHORT).show()
        }else{ //모두 정상입력
            val intent = Intent(this, PutUpRoomActivity::class.java)
            val dto = RoomMoreInfoDTO(area, options, parking, term, movein)
            intent.putExtra("dto",dto)
            setResult(RESULT_OK,intent)
            finish()
        }
    }
}