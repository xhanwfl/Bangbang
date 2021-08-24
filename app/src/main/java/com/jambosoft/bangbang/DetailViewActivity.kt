package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.model.ContentDTO
import java.text.SimpleDateFormat

class DetailViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)

        val titleTextView = findViewById<TextView>(R.id.detailview_title_textview)
        val contentTextView = findViewById<TextView>(R.id.detailview_content_textview)
        val userIdTextView = findViewById<TextView>(R.id.detailview_userid_textview)
        val timestampTextView = findViewById<TextView>(R.id.detailview_timestamp_textview)

        var timestamp = intent.getLongExtra("timestamp",0)
        Log.e("timestamp"," : ${timestamp}")

        val db = FirebaseFirestore.getInstance()
        db.collection("contents").whereEqualTo("timestamp",timestamp).get().addOnSuccessListener { task ->
            var dto = ContentDTO()
            for(document in task){
                dto = document.toObject(ContentDTO::class.java)
            }

            titleTextView.text = dto.title
            contentTextView.text = dto.content
            userIdTextView.text = dto.userId
            var sdf = SimpleDateFormat("yyyy-MM-dd")
            var time = sdf.format(timestamp)
            timestampTextView.text = time
        }
    }
}