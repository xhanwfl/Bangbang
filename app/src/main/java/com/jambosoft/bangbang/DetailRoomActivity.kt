package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.jambosoft.bangbang.Adapter.DetailRoomImageAdapter
import com.jambosoft.bangbang.Adapter.SelectedImageAdapter
import com.jambosoft.bangbang.model.RoomDTO
import org.w3c.dom.Text

class DetailRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_room)

        var dto = intent.getSerializableExtra("dto") as RoomDTO
        Log.e("detailroom", dto.address.toString())
        //방 이미지 페이저
        val imageViewPager = findViewById<ViewPager>(R.id.detailroom_image_pager)

        //val adapter = DetailRoomImageAdapter(list)

        //보증금, 월세
        val priceTextView = findViewById<TextView>(R.id.detailroom_price_textview)

        //주소
        val addressTextView = findViewById<TextView>(R.id.detailroom_address_textview)

        //방구조
        val roomkindsTextView = findViewById<TextView>(R.id.detailroom_roomkinds_textview)

        //관리비
        val adminFeeTextView = findViewById<TextView>(R.id.detailroom_adminfee_textview)

        //층 수
        val floarTextView = findViewById<TextView>(R.id.detailroom_floar_textview)

        //제목
        val titleTextView = findViewById<TextView>(R.id.detailroom_title_textview)

        //내용
        val explainTextView = findViewById<TextView>(R.id.detailroom_explain_textview)

        //날짜
        val dateTextView = findViewById<TextView>(R.id.detailroom_date_textview)

        //면적
        val areaTextView = findViewById<TextView>(R.id.detailroom_area_textview)

        //옵션
        val optionTextView = findViewById<TextView>(R.id.detailroom_option_textview)

        //주차 여부
        val parkingTextView = findViewById<TextView>(R.id.detailroom_parking_textview)

        //이용 기간
        val termTextView = findViewById<TextView>(R.id.detailroom_term_textview)

        //입주 가능 날짜
        val moveInTextView = findViewById<TextView>(R.id.detailroom_movein_textview)

        //유저아이디
        val userIdTextView = findViewById<TextView>(R.id.detailroom_userid_textview)

        //유저 프로필이미지
        val userProfileImageView = findViewById<ImageView>(R.id.detailroom_profile_imageview)

    }
}