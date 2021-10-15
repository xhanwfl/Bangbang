package com.jambosoft.bangbang

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.net.toUri
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jambosoft.bangbang.Adapter.SelectedImageAdapter
import com.jambosoft.bangbang.Dialog.LoadingDialog
import com.jambosoft.bangbang.model.*

class PutUpRoomActivity : AppCompatActivity() {
    var listUri: ArrayList<String>? = null
    var listUrl : ArrayList<String>? = null
    var roomDTO : RoomDTO? = null
    var roomInfoDTO : RoomInfoDTO? = null
    var roomMoreInfoDTO : RoomMoreInfoDTO? = null
    var roomLocationInfoDTO : RoomLocationInfoDTO? = null
    var roomkinds : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_put_up_room)
        listUri = arrayListOf()

        //뒤로가기 버튼
        val backButton = findViewById<Button>(R.id.putup_room_back_btn)
        backButton.setOnClickListener {
            finish()
        }

        //이미지 올리기
        val cameraButton = findViewById<TextView>(R.id.putup_room_camera_textview)
        cameraButton.setOnClickListener {
            getImage()
        }
        val cameraImage = findViewById<ImageView>(R.id.putup_room_camera_imageview)
        cameraImage.setOnClickListener {
            getImage()
        }


        //주소찾기 버튼
        val locationButton = findViewById<Button>(R.id.putup_room_location_btn)
        locationButton.setOnClickListener {
            getLocation()
        }

        //설명쓰기 버튼
        val roomInfoButton = findViewById<Button>(R.id.putup_room_info_btn)
        roomInfoButton.setOnClickListener {
            getInfo()
        }

        //상세정보 버튼
        val roomMoreInfoButton = findViewById<Button>(R.id.putup_room_more_info_btn)
        roomMoreInfoButton.setOnClickListener {
            getMoreInfo()
        }

        //방 올리기 버튼
        val putUpRoomButton = findViewById<TextView>(R.id.putup_room_btn)
        putUpRoomButton.setOnClickListener {
            putUpRoom()
        }



        //라디오 버튼
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener{ radioGroup, i ->
            when(i){
                R.id.putup_room_oneroom_radiobtn ->{
                    roomkinds = false
                }
                R.id.putup_room_sharehouse_radiobtn ->{
                    roomkinds = true
                }
            }
        }





    }
    fun putUpRoom(){
        if(listUri!=null&& listUri!!.isNotEmpty()&&roomLocationInfoDTO!=null&&
                roomInfoDTO!=null&&roomMoreInfoDTO!=null){
            val depositEditText = findViewById<EditText>(R.id.putup_room_deposit_edittext)
            val depositText = depositEditText.text.toString()
            val deposit = depositEditText.text.toString().toInt()

            val monthlyfeeEditText = findViewById<EditText>(R.id.putup_room_monthlyfee_edittext)
            val monthlyfeeText = monthlyfeeEditText.text.toString()
            val monthlyfee = monthlyfeeEditText.text.toString().toInt()

            val adminfeeEditText = findViewById<EditText>(R.id.putup_room_adminfee_edittext)
            val adminfeeText = adminfeeEditText.text.toString()
            val adminfee = adminfeeEditText.text.toString().toInt()

            val floorNumberEditText = findViewById<EditText>(R.id.putup_room_floornumber_edittext)
            val floorNumber = floorNumberEditText.text.toString()

            if(depositText.equals("")||monthlyfeeText.equals("")||adminfeeText.equals("")||floorNumber.equals("")){ //edittext를 입력안할경우
                Toast.makeText(this,"내용을 모두 입력해주세요",Toast.LENGTH_SHORT).show()

            }else{ //모두 입력할경우
                val loadingDialog = LoadingDialog(this) //로딩 보여주기
                loadingDialog.show()
                var user = FirebaseAuth.getInstance().currentUser
                val currentTime = System.currentTimeMillis()
                val imageCount = listUri!!.size
                listUrl = arrayListOf()

                //스토리지에 업로드
                val storageRef = FirebaseStorage.getInstance().reference.child("roomImages/${currentTime}")
                for(i : Int in 0..listUri!!.size-1){
                    //업로드는 비동기로 처리되어있어서 콜백함수를 사용해야 결과값을 바로 사용할수있다. 단점 : 업로드가 느리다
                        val ref = storageRef?.child(i.toString()+".jpg")
                        val uploadTask = ref.putFile(listUri!![i].toUri()).addOnSuccessListener {
                        Log.d("!PutUpRoomActivity","ref.downloadUrl : ${ref.downloadUrl}")
                            ref.downloadUrl.addOnSuccessListener {
                                Log.d("!PutUpRoomActivity","Uri.toString : ${it}")
                                listUrl!!.add(it.toString())

                                if(listUrl!!.size==listUri!!.size){//url이 리스트에 다 추가되었을때
                                    roomDTO = RoomDTO(listUrl!!,roomLocationInfoDTO!!,deposit,monthlyfee, adminfee, //roomDTO 초기화
                                        floorNumber, roomkinds,roomInfoDTO!!,roomMoreInfoDTO!!,user!!.email!!,user.uid,currentTime,imageCount)

                                    //firestore에 업로드
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("rooms").document(roomDTO!!.timestamp.toString())
                                        .set(roomDTO!!).addOnSuccessListener {
                                            Log.d("!PutUpRoomActivity","성공")
                                            Toast.makeText(this,"업로드 성공",Toast.LENGTH_SHORT).show()
                                            loadingDialog.cancel()
                                            finish()
                                        }.addOnFailureListener{
                                            Log.d("!PutUpRoomActivity","실패")
                                            Toast.makeText(this,"업로드 실패",Toast.LENGTH_SHORT).show()

                                        }

                                    Log.e("dto","${roomDTO!!.info.title} \n ${roomDTO!!.moreInfo.movein} \n ${roomkinds}")
                                }
                            }
                    }
                }
            }
        }else{ //제대로 입력 안되어있을경우
            Toast.makeText(this,"내용을 입력하세요",Toast.LENGTH_SHORT).show()
        }
    }

    fun getInfo(){
        var intent = Intent(this,PutUpRoomInfoActivity::class.java)
        if(roomInfoDTO!=null){
            intent.putExtra("dto",roomInfoDTO)
        }else{
            roomInfoDTO = RoomInfoDTO()
            intent.putExtra("dto",roomInfoDTO)
        }
        startActivityForResult(intent, 400)
    }

    fun getMoreInfo(){
        var intent = Intent(this,PutUpRoomMoreInfoActivity::class.java)
        if(roomMoreInfoDTO!=null){
            intent.putExtra("dto",roomMoreInfoDTO)
        }else{
            roomMoreInfoDTO = RoomMoreInfoDTO()
            intent.putExtra("dto",roomMoreInfoDTO)
        }
        startActivityForResult(intent, 500)
    }

    //방 위치정보 가져오기
    fun getLocation(){
        startActivityForResult(Intent(this,SearchActivity::class.java),300)

    }

    //방사진 가져오기
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
            listUri?.clear()
            if (data?.clipData != null) { //이미지가 여러장일경우
                val count = data?.clipData!!.itemCount
                if (count > 10) { //이미지가 10장이상일 경우 리턴
                    Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG)
                    return
                }
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    listUri?.add(imageUri.toString())
                }
            } else {  //이미지가 한장일경우
                data?.data?.let {
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        listUri?.add(imageUri.toString())
                    }
                }
            }
            val adapter = SelectedImageAdapter(listUri!!)
            val pager = findViewById<ViewPager>(R.id.putup_room_image_pager)
            pager.adapter = adapter
        }


        //주소 검색 이벤트
        if(resultCode == RESULT_OK && requestCode == 300){
            roomLocationInfoDTO = data?.getSerializableExtra("dto") as RoomLocationInfoDTO

            val addressTextView = findViewById<TextView>(R.id.putup_room_address_textview)
            addressTextView.text = roomLocationInfoDTO!!.address
            addressTextView.visibility = View.VISIBLE

        }

        //설명 쓰기 이벤트
        if(resultCode==RESULT_OK && requestCode == 400){
            roomInfoDTO = data?.getSerializableExtra("dto") as RoomInfoDTO

            val titleTextView = findViewById<TextView>(R.id.putup_room_title_textview)
            titleTextView.text = roomInfoDTO!!.title
            titleTextView.visibility = View.VISIBLE
        }

        //상세 정보 이벤트
        if(resultCode==RESULT_OK && requestCode == 500){
            roomMoreInfoDTO = data?.getSerializableExtra("dto") as RoomMoreInfoDTO
            val infoTextView = findViewById<TextView>(R.id.putup_room_moreinfo_textview)
            infoTextView.text = roomMoreInfoDTO!!.kinds
            infoTextView.visibility = View.VISIBLE

            Log.e("data", roomMoreInfoDTO!!.kinds)
        }
    }
}