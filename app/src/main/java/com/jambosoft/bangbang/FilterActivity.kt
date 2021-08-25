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
    var latitude : String? = null
    var longitude : String? = null
    var depositFee : Int = 0
    var monthlyFee : Int = 0
    var roomkinds : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        roomkinds = intent.getBooleanExtra("roomkinds",false)

        //검색 버튼
        var locationButton = findViewById<Button>(R.id.filter_search_btn)
        locationButton.setOnClickListener {
            getLocation()
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
                    depositFee = 10000
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
                    monthlyFee = 500
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
        if(latitude==null||longitude==null){
            Toast.makeText(this,"주소를 입력해주세요",Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(this,KakaoMapActivity::class.java)
            intent.putExtra("latitude",latitude)
            intent.putExtra("longitude",longitude)
            intent.putExtra("depositFee",depositFee)
            intent.putExtra("monthlyFee",monthlyFee)
            intent.putExtra("roomkinds",roomkinds)
            startActivity(intent)
            finish()
        }
    }

    //검색기능 -> 검색창으로이동
    fun getLocation(){
        startActivityForResult(Intent(this,SearchActivity::class.java),300)
    }

    //검색결과 받아오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //주소 검색 이벤트
        if(resultCode == RESULT_OK && requestCode == 300){
            latitude = data?.getStringExtra("latitude")
            longitude = data?.getStringExtra("longitude")

        }
    }
}