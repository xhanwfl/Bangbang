package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.Adapter.InquireListAdapter
import com.jambosoft.bangbang.model.InquireDTO
import com.jambosoft.bangbang.model.RoomDTO

class InquireListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquire_list)

        val timestamp = intent.getStringExtra("timestamp").toString()  //방 id를 가져옴
        var itemList : ArrayList<InquireDTO> = arrayListOf()
        val glideRequestManager = Glide.with(this)

        //닫기버튼
        val closeButton = findViewById<Button>(R.id.inquirelist_close_btn)
        closeButton.setOnClickListener {
            finish()
        }

        //recyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.inquirelist_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        FirebaseFirestore.getInstance().collection("inquire").whereEqualTo("roomId",timestamp).get().addOnSuccessListener { documents ->
            for(document in documents){
                var inquire = document.toObject(InquireDTO::class.java)
                itemList.add(inquire)
            }
            recyclerView.adapter = InquireListAdapter(itemList,glideRequestManager) // glideRequestManager을 사용해야 로딩중 activity가 종료됐을경우 exception을 발생시키지않음
        }


    }
}