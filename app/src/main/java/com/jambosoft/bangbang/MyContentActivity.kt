package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.Adapter.CommunityFragmentContentAdapter
import com.jambosoft.bangbang.Adapter.MyRoomAdapter
import com.jambosoft.bangbang.model.ContentDTO
import com.jambosoft.bangbang.model.RoomDTO

class MyContentActivity : AppCompatActivity() {
    lateinit var recyclerView : RecyclerView
    lateinit var user : FirebaseUser
    lateinit var db : FirebaseFirestore
    lateinit var roomDTOList : ArrayList<RoomDTO>
    lateinit var contentDTOList : ArrayList<ContentDTO>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_content)

        user = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()
        roomDTOList = arrayListOf()
        contentDTOList = arrayListOf()

        recyclerView = findViewById(R.id.mycontent_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val kindTextView = findViewById<TextView>(R.id.mycontent_kind_textview)

        val kind = intent.getStringExtra("kind").toString()
        if(kind.equals("rooms")){
            kindTextView.text = "내가 올린 방"
            val putupRoomLayout = findViewById<ConstraintLayout>(R.id.mycontent_putup_room_layout)
            putupRoomLayout.visibility = View.VISIBLE

            val putupRoomImageView = findViewById<ImageView>(R.id.mycontent_putup_room_imageview)
            putupRoomImageView.setOnClickListener {
                finish()
            }

            setRooms()
        }else if(kind.equals("contents")){
            kindTextView.text = "내 게시물"
            setContents()
        }




    }

    fun setRooms(){
        db.collection("rooms").whereEqualTo("userId",user.email).get().addOnSuccessListener {
            roomDTOList.clear()

            for(document in it){
                var dto = document.toObject(RoomDTO::class.java)
                roomDTOList.add(dto)

            }

            recyclerView.adapter = MyRoomAdapter(roomDTOList)
        }
    }

    fun setContents(){
        db.collection("contents").whereEqualTo("uid",user.uid).get().addOnSuccessListener {
            contentDTOList.clear()
            for(document in it){
                var dto = document.toObject(ContentDTO::class.java)
                contentDTOList.add(dto)
            }

            recyclerView.adapter = CommunityFragmentContentAdapter(contentDTOList)
        }
    }
}