package com.jambosoft.bangbang

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.jambosoft.bangbang.Adapter.DetailRoomImageDetailAdapter

class DetailRoomImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_room_image)

        //imageList, position 가져오기
        val list = intent.getStringArrayListExtra("list") as ArrayList<String>
        val position = intent.getIntExtra("position", 0)

        //pager position textview
        val textView = findViewById<TextView>(R.id.detailroom_image_textview)
        textView.text = "${position+1}/${list.size}"

        //뒤로가기버튼
        val backButton = findViewById<Button>(R.id.detailroom_image_back_btn)
        backButton.setOnClickListener {
            finish()
        }

        //방 이미지 페이저
        val viewPager = findViewById<ViewPager>(R.id.detailroom_image_pager)
        val adapter = DetailRoomImageDetailAdapter(list!!)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(position)

        //pager event 감지
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            @SuppressLint("MissingSuperCall")
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                textView.text = "${position+1}/${list.size}"
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }
}