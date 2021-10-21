package com.jambosoft.bangbang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.jambosoft.bangbang.model.RoomLocationInfoDTO

class FilterActivity : AppCompatActivity() {
    var depositFee : Int = 0
    var monthlyFee : Int = 0
    var roomkinds : Int = 0
    lateinit var allTextView: TextView
    lateinit var sharehouseTextView: TextView
    lateinit var oneroomTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        //방 종류
        allTextView = findViewById(R.id.filter_all_textview)
        sharehouseTextView = findViewById(R.id.filter_sharehouse_textview)
        oneroomTextView = findViewById(R.id.filter_oneroom_textview)

        //모두보기
        allTextView.setOnClickListener {
            setRoomKindBackgroundColor(0)
            roomkinds = 0
        }
        //쉐어하우스
        sharehouseTextView.setOnClickListener {
            setRoomKindBackgroundColor(1)
            roomkinds = 1
        }
        //원룸
        oneroomTextView.setOnClickListener {
            setRoomKindBackgroundColor(2)
            roomkinds = 2
        }


        //적용하기 버튼
        var applyButton = findViewById<Button>(R.id.filter_apply_btn)
        applyButton.setOnClickListener {
            applyFilter()
        }

        //뒤로가기 버튼
        var backButton = findViewById<Button>(R.id.filter_back_btn)
        backButton.setOnClickListener {
            finish()
        }

        //보증금 seekBar
        val text = findViewById<TextView>(R.id.filter_seekbar_textview)
        val seekBar = findViewById<SeekBar>(R.id.filter_seekbar)
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress<100){
                    text.text = "없음"
                } else if(progress<=1000){
                    depositFee = progress-(progress%100)
                    text.text = "${depositFee}만원 이하"
                } else {
                    depositFee = 100000
                    text.text = "전체보기"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        //월세 seekBar
        val text2 = findViewById<TextView>(R.id.filter_seekbar2_textview)
        val seekBar2 = findViewById<SeekBar>(R.id.filter_seekbar2)
        seekBar2.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress<=100){
                    monthlyFee = progress
                    text2.text = "${monthlyFee}만원 이하"
                }else{
                    monthlyFee = 10000
                    text2.text = "전체보기"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

    }

    //적용하기 -> 맵으로이동
    fun applyFilter(){
        val intent = Intent(this,KakaoMapActivity::class.java)
        intent.putExtra("depositFee",depositFee)
        intent.putExtra("monthlyFee",monthlyFee)
        intent.putExtra("roomkinds",roomkinds)
        startActivity(intent)
        finish()

    }

    fun setRoomKindBackgroundColor(position : Int){
        allTextView.setBackgroundColor(this.resources.getColor(R.color.white))
        sharehouseTextView.setBackgroundColor(this.resources.getColor(R.color.white))
        oneroomTextView.setBackgroundColor(this.resources.getColor(R.color.white))
        when(position){
            0 ->{
                allTextView.setBackgroundColor(this.resources.getColor(R.color.gray))
            }
            1 ->{
                sharehouseTextView.setBackgroundColor(this.resources.getColor(R.color.gray))
            }
            2 ->{
                oneroomTextView.setBackgroundColor(this.resources.getColor(R.color.gray))
            }
        }
    }
}