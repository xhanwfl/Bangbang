package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import net.daum.mf.map.api.MapView

class KakaoMapActivity : AppCompatActivity() {
    var latitude : Double = 0.0
    var longitude : Double = 0.0
    var depositFee : Int = 0
    var monthlyFee : Int = 0
    var roomkinds : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_map)

        latitude = intent.getStringExtra("latitude")!!.toDouble()
        longitude = intent.getStringExtra("longitude")!!.toDouble()
        depositFee = intent.getIntExtra("depositFee",0)
        monthlyFee = intent.getIntExtra("monthlyFee",0)
        roomkinds = intent.getStringExtra("roomkinds")!!.toString()

        Log.e("wow","latitude : ${latitude}\nlongitude : ${longitude}\ndepositFee : ${depositFee}\nmonthlyFee : ${monthlyFee}\nroomkinds : ${roomkinds}\n")
        var mapView = MapView(this)

        var mapViewContainer = findViewById<ViewGroup>(R.id.map_view)
        mapViewContainer.addView(mapView)
    }
}