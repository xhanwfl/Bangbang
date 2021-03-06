package com.jambosoft.bangbang

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.Adapter.DetailRoomImageAdapter
import com.jambosoft.bangbang.model.InquireDTO
import com.jambosoft.bangbang.model.RoomDTO
import com.jambosoft.bangbang.model.UserInfoDTO
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import java.text.SimpleDateFormat

class DetailRoomActivity : AppCompatActivity() {
    var uriList : ArrayList<Uri>? = null
    var user : FirebaseUser? = null
    var dto : RoomDTO? = null
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_room)

        //초기화
        db = FirebaseFirestore.getInstance()
        uriList = arrayListOf()
        user = FirebaseAuth.getInstance().currentUser




        dto = intent.getSerializableExtra("dto") as RoomDTO
        Log.e("detailroom", dto?.address.toString())


        //최근본 사람 저장
        setRecentView(dto!!)

        //공유하기버튼
        val shareButton = findViewById<Button>(R.id.detailroom_share_btn)
        shareButton.setOnClickListener {
            Toast.makeText(this,"준비중입니다.",Toast.LENGTH_SHORT).show()
        }

        //back버튼
        val backButton = findViewById<Button>(R.id.detailroom_back_btn)
        backButton.setOnClickListener {
            finish()
        }

        //좋아요버튼
        val favoriteButton = findViewById<Button>(R.id.detailroom_favorite_btn)
        if(dto!!.favorites[user!!.uid] != null){
            if(dto!!.favorites[user!!.uid]!!){
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
            }
        }

        //문의하기버튼
        val inquireButton = findViewById<Button>(R.id.detailroom_inquire_btn)
        inquireButton.setOnClickListener {
            //dialog 띄우기
            showInquireDialog()
       }

        //좋아요버튼
        favoriteButton.setOnClickListener {
            if (dto!!.favorites[user!!.uid] == null){ //널인경우
                dto!!.favorites[user!!.uid] = true
                dto!!.favoriteCount++
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
            }else{ //널이아닐경우
               if(!dto!!.favorites[user!!.uid]!!){ //좋아요를 누른 경우
                   dto!!.favorites[user!!.uid] = true
                   dto!!.favoriteCount++
                   favoriteButton.setBackgroundResource(R.drawable.ic_favorite)
               }else{ //좋아요를 한상태에서 한번더 누를경우
                   dto!!.favorites[user!!.uid] = false
                   dto!!.favoriteCount--
                   favoriteButton.setBackgroundResource(R.drawable.ic_favorite_white)
                }
            }

            FirebaseFirestore.getInstance().collection("rooms").document(dto!!.timestamp.toString()).set(dto!!).addOnSuccessListener {
                Log.e("detailroom" , "좋아요 성공")
            }.addOnFailureListener {
                Log.e("detailroom" , "좋아요 실패")
            }
        }






        //보증금, 월세
        val priceTextView = findViewById<TextView>(R.id.detailroom_price_textview)
        if(dto!!.contractType==0){ //월세
            priceTextView.text = "월세 ${dto?.deposit}/${dto?.monthlyFee}"
        }else{
            priceTextView.text = "전세 ${dto?.deposit}"
        }

        //타이틀2
        val titleTextView2 = findViewById<TextView>(R.id.detailroom_title_textview2)
        titleTextView2.text = dto?.info?.title

        //방구조
        val roomkindsTextView = findViewById<TextView>(R.id.detailroom_roomkinds_textview)
        var roomKindsText = "원룸"
        when(dto!!.roomKinds){
            0 ->{ roomKindsText = "원룸" }
            1 ->{ roomKindsText = "투·쓰리룸" }
            2 ->{ roomKindsText = "오피스텔" }
            3 ->{ roomKindsText = "쉐어하우스" }
        }
        roomkindsTextView.text = roomKindsText

        //관리비
        val adminFeeTextView = findViewById<TextView>(R.id.detailroom_adminfee_textview)
        adminFeeTextView.text = "${dto?.adminFee}만"

        //층 수
        val floorTextView = findViewById<TextView>(R.id.detailroom_floor_textview)
        if(dto!!.floorNumber==0){
            floorTextView.text = "반지층"
        }else{
            floorTextView.text = "${dto?.floorNumber}층"
        }

        //제목
        val titleTextView = findViewById<TextView>(R.id.detailroom_title_textview)
        titleTextView.text = dto?.info?.title

        //내용
        val explainTextView = findViewById<TextView>(R.id.detailroom_explain_textview)
        explainTextView.text = "${dto?.info?.explain}"

        //날짜
        val dateTextView = findViewById<TextView>(R.id.detailroom_date_textview)
        var sdf = SimpleDateFormat("yyyy-MM-dd")
        var time = sdf.format(dto?.timestamp)
        dateTextView.text = time

        //방 종류
        val kindTextView = findViewById<TextView>(R.id.detailroom_kind_textview)
        kindTextView.text = "${roomKindsText}"

        //면적
        val areaTextView = findViewById<TextView>(R.id.detailroom_area_textview)
        areaTextView.text = "${dto?.moreInfo?.area} (제곱미터)"

        //옵션
        val optionTextView = findViewById<TextView>(R.id.detailroom_option_textview)
        optionTextView.text = "${dto?.moreInfo?.options}"

        //주차 여부
        val parkingTextView = findViewById<TextView>(R.id.detailroom_parking_textview)
        parkingTextView.text = "${dto?.moreInfo?.parking}"


        //이용 기간
        val termTextView = findViewById<TextView>(R.id.detailroom_term_textview)
        termTextView.text = "${dto?.moreInfo?.term}"

        //입주 가능 날짜
        val moveInTextView = findViewById<TextView>(R.id.detailroom_movein_textview)
        moveInTextView.text = "${dto?.moreInfo?.movein}"

        //유저아이디
        val userIdTextView = findViewById<TextView>(R.id.detailroom_userid_textview)
        userIdTextView.text = dto?.userId

        //유저 프로필이미지
        val userProfileImageView = findViewById<ImageView>(R.id.detailroom_profile_imageview)
        db.collection("userInfo").document(dto!!.uid).get().addOnSuccessListener { document ->
            var userDTO = document.toObject<UserInfoDTO>()
            Glide.with(this).load(userDTO!!.profileUrl.toUri()).thumbnail(0.1f).apply(
                RequestOptions().centerCrop()).into(userProfileImageView)
        }

        //주소
        val addressTextView = findViewById<TextView>(R.id.detailroom_address_textview)
        addressTextView.text = dto?.address?.address



        //전화걸기
        val callButton = findViewById<Button>(R.id.detailroom_call_btn)
        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:${dto?.hp}"))
            startActivity(intent)
        }


        //방 이미지 페이저
        val imageViewPager = findViewById<ViewPager>(R.id.detailroom_pager)
//        //getRoomImages(dto!!)
//        for(i in 0 until dto!!.imageCount){
//            uriList!!.add(dto!!.images[i].toUri())
//        }

        val adapter = DetailRoomImageAdapter(dto!!.images!!)
        adapter.ViewPagerAdapter(this)
        imageViewPager!!.adapter = adapter

        //indicator
        val indicator = findViewById<SpringDotsIndicator>(R.id.detailroom_image_indicator)
        indicator.setViewPager(imageViewPager)


    }

    fun showInquireDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_inquest_room, null)
        builder.setView(dialogView)
            .setPositiveButton("보내기") { dialogInterface, i ->  //보내기버튼
                val messageEditText = dialogView.findViewById<EditText>(R.id.dialog_inquire_message_edittext)
                var message = messageEditText.text.toString()
                val hpEditText = dialogView.findViewById<EditText>(R.id.dialog_inquire_hp_edittext)
                var hp = hpEditText.text.toString()
                if(message.equals("")||hp.equals("")){ //입력 안할경우
                    Toast.makeText(this,"내용과 전화번호를 모두 입력해주세요",Toast.LENGTH_SHORT).show()
                }else{  //모두 입력했을경우 메세지 보내기
                    db.collection("userInfo").document(user!!.uid).get().addOnSuccessListener { document -> //currentUser의 userId를 가져온다
                        if (document != null) {
                            Log.e("프로필", "가져오기 성공")
                            val userInfoDTO = document.toObject<UserInfoDTO>()!!
                            val timestamp = System.currentTimeMillis()

                            val inquire =   InquireDTO(user!!.uid,userInfoDTO.name,hp,message,dto!!.timestamp.toString(),dto!!.uid,timestamp)
                            db.collection("inquire").document(inquire.timestamp.toString()).set(inquire).addOnSuccessListener {
                                    dto!!.inquireCount++
                                    db.collection("rooms").document(dto!!.timestamp.toString()).set(dto!!) // roomdto에 문의 count 설정
                                    db.collection("userInfo").document(dto!!.uid).get().addOnSuccessListener { //방올린사람 정보 가져와서
                                        val roomUserInfo = it.toObject<UserInfoDTO>()
                                        roomUserInfo!!.alramCount++
                                        db.collection("userInfo").document(dto!!.uid).set(roomUserInfo) //알람 카운트 +1
                                    }

                                    Toast.makeText(this,"문의 완료",Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener{
                                    Toast.makeText(this,"전송 실패",Toast.LENGTH_SHORT).show()
                                }
                        } else {  //document가 null
                            Log.d("!DetailRoomActivity", "userInfoDTO : null")
                        }
                    }
                }
            }
            .setNegativeButton("취소") { dialogInterface, i ->  //취소버튼
                Toast.makeText(this,"취소",Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    fun setRecentView(dto : RoomDTO){
        if(dto.recents[user!!.uid]==null){
            dto.recents.put(user!!.uid,System.currentTimeMillis())
        }else{
            dto.recents[user!!.uid] = System.currentTimeMillis()
        }

        FirebaseFirestore.getInstance().collection("rooms").document(dto.timestamp.toString()).set(dto)
    }
}