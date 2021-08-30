package com.jambosoft.bangbang

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.Adapter.DetailRoomImageAdapter
import com.jambosoft.bangbang.model.RoomDTO
import java.text.SimpleDateFormat

class DetailRoomActivity : AppCompatActivity() {
    var imageViewPager : ViewPager? = null
    var adapter : DetailRoomImageAdapter? = null
    var uriList : ArrayList<Uri>? = null
    var user : FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_room)

        uriList = arrayListOf()

        user = FirebaseAuth.getInstance().currentUser




        val dto = intent.getSerializableExtra("dto") as RoomDTO
        Log.e("detailroom", dto.address.toString())


        //최근본 사람 저장
        setRecentView(dto)

        //back버튼
        val backButton = findViewById<Button>(R.id.detailroom_back_btn)
        backButton.setOnClickListener {
            finish()
        }

        //좋아요버튼
        val favoriteButton = findViewById<Button>(R.id.detailroom_favorite_btn)
        if(dto.favorites[user!!.uid] != null){
            if(dto.favorites[user!!.uid]!!){
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
            }
        }


        favoriteButton.setOnClickListener {
            if (dto.favorites[user!!.uid] == null){ //널인경우
                dto.favorites[user!!.uid] = true
                dto.favoriteCount++
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
            }else{ //널이아닐경우
               if(!dto.favorites[user!!.uid]!!){ //좋아요를 누른 경우
                   dto.favorites[user!!.uid] = true
                   dto.favoriteCount++
                   favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
               }else{ //좋아요를 한상태에서 한번더 누를경우
                   dto.favorites[user!!.uid] = false
                   dto.favoriteCount--
                   favoriteButton.setBackgroundResource(R.drawable.ic_favorite_white)
                }
            }

            FirebaseFirestore.getInstance().collection("rooms").document(dto.timestamp.toString()).set(dto).addOnSuccessListener {
                Log.e("detailroom" , "좋아요 성공")
            }.addOnFailureListener {
                Log.e("detailroom" , "좋아요 실패")
            }

        }






        //보증금, 월세
        val priceTextView = findViewById<TextView>(R.id.detailroom_price_textview)
        priceTextView.text = "월세 ${dto.deposit}/${dto.monthlyFee}"

        //주소
        val addressTextView = findViewById<TextView>(R.id.detailroom_address_textview)
        addressTextView.text = dto.address.address

        //방구조
        val roomkindsTextView = findViewById<TextView>(R.id.detailroom_roomkinds_textview)
        roomkindsTextView.text = if(dto.roomKinds) "쉐어하우스" else "원룸"

        //관리비
        val adminFeeTextView = findViewById<TextView>(R.id.detailroom_adminfee_textview)
        adminFeeTextView.text = "${dto.adminFee}만"

        //층 수
        val floorTextView = findViewById<TextView>(R.id.detailroom_floor_textview)
        floorTextView.text = "${dto.floorNumber}층"

        //제목
        val titleTextView = findViewById<TextView>(R.id.detailroom_title_textview)
        titleTextView.text = dto.info.title

        //내용
        val explainTextView = findViewById<TextView>(R.id.detailroom_explain_textview)
        explainTextView.text = "${dto.info.explain}"

        //날짜
        val dateTextView = findViewById<TextView>(R.id.detailroom_date_textview)
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        var time = sdf.format(dto.timestamp)
        dateTextView.text = time

        //면적
        val areaTextView = findViewById<TextView>(R.id.detailroom_area_textview)
        areaTextView.text = "  ${dto.moreInfo.area} (제곱미터)"

        //옵션
        val optionTextView = findViewById<TextView>(R.id.detailroom_option_textview)
        optionTextView.text = "  ${dto.moreInfo.options}"

        //주차 여부
        val parkingTextView = findViewById<TextView>(R.id.detailroom_parking_textview)
        parkingTextView.text = "  ${dto.moreInfo.parking}"


        //이용 기간
        val termTextView = findViewById<TextView>(R.id.detailroom_term_textview)
        parkingTextView.text = "  ${dto.moreInfo.term}"

        //입주 가능 날짜
        val moveInTextView = findViewById<TextView>(R.id.detailroom_movein_textview)
        moveInTextView.text = "  ${dto.moreInfo.movein}"

        //유저아이디
        val userIdTextView = findViewById<TextView>(R.id.detailroom_userid_textview)
        userIdTextView.text = dto.userId

        //유저 프로필이미지
        val userProfileImageView = findViewById<ImageView>(R.id.detailroom_profile_imageview)


        //방 이미지 페이저
        imageViewPager = findViewById<ViewPager>(R.id.detailroom_image_pager)
        getRoomImages(dto)

        val handler = android.os.Handler()
        handler.postDelayed({
            adapter = DetailRoomImageAdapter(uriList!!)
            imageViewPager!!.adapter = adapter
        }, 2000)

    }

    fun setRecentView(dto : RoomDTO){
        if(dto.recents[user!!.uid]==null){
            dto.recents.put(user!!.uid,System.currentTimeMillis())
        }else{
            dto.recents[user!!.uid] = System.currentTimeMillis()
        }

        FirebaseFirestore.getInstance().collection("rooms").document(dto.timestamp.toString()).set(dto)
    }

    fun getRoomImages(dto : RoomDTO){
        val storage = FirebaseStorage.getInstance()
        val ref = storage.reference.child("roomImages/${dto.timestamp}")
        uriList!!.clear()

            for(i in 0..dto.imageCount-1){
                ref.child("${i}.jpg").downloadUrl.addOnSuccessListener {
                    uriList!!.add(it)
                    Log.e("getRoomImages",it.toString())


                }.addOnFailureListener {
                    Log.e("getRoomImages","onFailureListener")
                }
            }
    }
}