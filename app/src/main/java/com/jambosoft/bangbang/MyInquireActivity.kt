package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MyInquireActivity : AppCompatActivity() {
    lateinit var user : FirebaseUser
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_inquire)

        user = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()


        //닫기버튼
        val closeButton = findViewById<Button>(R.id.myinquire_close_btn)
        closeButton.setOnClickListener {
            finish()
        }

        //recyclerView
        val myInquireRecyclerView = findViewById<RecyclerView>(R.id.myinquire_recycler)
        myInquireRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("inquire").whereEqualTo("uid",user.uid).get().addOnSuccessListener { documents ->
            for(document in documents){

            }
        }


    }
}