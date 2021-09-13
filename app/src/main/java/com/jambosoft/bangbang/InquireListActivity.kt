package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.Adapter.InquireListAdapter
import com.jambosoft.bangbang.model.RoomDTO

class InquireListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquire_list)

        val timestamp = intent.getStringExtra("timestamp").toString()
        var itemList : ArrayList<RoomDTO.Inquire> = arrayListOf()

        //닫기버튼
        val closeButton = findViewById<Button>(R.id.inquirelist_close_btn)
        closeButton.setOnClickListener {
            finish()
        }

        //recyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.inquirelist_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        FirebaseFirestore.getInstance().collection("rooms").document(timestamp).collection("inquire").get().addOnSuccessListener { documents ->
            for(document in documents){
                var inquire = document.toObject(RoomDTO.Inquire::class.java)
                itemList.add(inquire)
            }
            recyclerView.adapter = InquireListAdapter(itemList)
        }


    }
}