package com.jambosoft.bangbang.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.Adapter.CommunityFragmentRecentAdapter
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.WriteContentActivity
import com.jambosoft.bangbang.model.ContentDTO

class CommunityFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val rootView : View = inflater.inflate(R.layout.fragment_community, container, false)

        val writeButton = rootView.findViewById<Button>(R.id.frag_community_write_btn)
        writeButton.setOnClickListener {
            startActivity(Intent(rootView.context,WriteContentActivity::class.java))
        }

        //content recyclerview setting
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.frag_community_content_recycler)
        recyclerView.layoutManager = LinearLayoutManager(rootView.context)

        //content 가져오기
        val db = FirebaseFirestore.getInstance()
        var contents : ArrayList<ContentDTO> = arrayListOf()
        db.collection("contents").get().addOnSuccessListener { documents ->
            for(document in documents){
                var dto = document.toObject(ContentDTO::class.java)
                contents.add(dto)
            }
            recyclerView.adapter = CommunityFragmentRecentAdapter(contents)
        }







        return rootView
    }


}