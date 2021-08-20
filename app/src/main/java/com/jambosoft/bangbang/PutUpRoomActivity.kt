package com.jambosoft.bangbang

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.jambosoft.bangbang.Adapter.SelectedImageAdapter

class PutUpRoomActivity : AppCompatActivity() {
    var list: ArrayList<Uri>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_put_up_room)
        list = arrayListOf()

        //이미지 올리기 버튼
        var cameraButton = findViewById<ImageView>(R.id.putup_room_camera_imageview)
        cameraButton.setOnClickListener {
            getImage()
        }

        //주소찾기 버튼
        var locationButton = findViewById<Button>(R.id.putup_room_location_btn)
        locationButton.setOnClickListener {
            getLocation()
        }







    }

    fun getLocation(){
        startActivityForResult(Intent(this,SearchActivity::class.java),300)

    }

    fun getImage(){
        var intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //이미지 선택 이벤트
        if (resultCode == RESULT_OK && requestCode == 200) {
            list?.clear()
            if (data?.clipData != null) { //이미지가 여러장일경우
                val count = data?.clipData!!.itemCount
                if (count > 10) { //이미지가 10장이상일 경우 리턴
                    Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG)
                    return
                }
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    list?.add(imageUri)
                }
            } else {  //이미지가 한장일경우
                data?.data?.let {
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        list?.add(imageUri)
                    }
                }
            }
            val adapter = SelectedImageAdapter(list!!)
            val pager = findViewById<ViewPager>(R.id.putup_room_image_pager)
            pager.adapter = adapter

        }


        //주소 검색 이벤트
        if(resultCode == RESULT_OK && requestCode == 300){
            var name = data?.getStringExtra("name")

            Log.e("data",name.toString())


        }





    }
}