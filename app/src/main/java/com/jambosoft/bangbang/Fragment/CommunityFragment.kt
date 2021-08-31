package com.jambosoft.bangbang.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jambosoft.bangbang.Adapter.CommunityFragmentRecentAdapter
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.WriteContentActivity
import com.jambosoft.bangbang.model.ContentDTO

class CommunityFragment : Fragment() {
    lateinit var db : FirebaseFirestore
    lateinit var recentItems : ArrayList<ContentDTO>
    lateinit var favoriteItems : ArrayList<ContentDTO>
    lateinit var recentView : RecyclerView
    lateinit var favoriteView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val rootView : View = inflater.inflate(R.layout.fragment_community, container, false)



        //content recyclerview setting
        recentView = rootView.findViewById<RecyclerView>(R.id.frag_community_recent_recycler)
        recentView.layoutManager = LinearLayoutManager(rootView.context)
        //favorite recyclerview setting
        favoriteView = rootView.findViewById<RecyclerView>(R.id.frag_community_favorite_recycler)
        favoriteView.layoutManager = LinearLayoutManager(rootView.context)

        //content 가져오기
        db = FirebaseFirestore.getInstance()
        recentItems = arrayListOf()
        favoriteItems = arrayListOf()

        //글쓰기버튼
        val writeButton = rootView.findViewById<Button>(R.id.frag_community_write_btn)
        writeButton.setOnClickListener {
            startActivity(Intent(rootView.context,WriteContentActivity::class.java))
        }

        //인기글 버튼
        val radioGroup = rootView.findViewById<RadioGroup>(R.id.content_radiogroup)
        radioGroup.setOnCheckedChangeListener{ radioGroup, i ->
            when(i){
                R.id.frag_community_recent_btn ->{
                    favoriteView.visibility = View.GONE
                    recentView.visibility = View.VISIBLE
                }
                R.id.frag_community_favorite_btn ->{
                    recentView.visibility = View.GONE
                    favoriteView.visibility = View.VISIBLE
                }
            }
        }



        setContent()






        return rootView
    }

    fun setContent(){
        db.collection("contents").orderBy("timestamp",Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            recentItems.clear()
            for(document in documents){
                var dto = document.toObject(ContentDTO::class.java)
                recentItems.add(dto)
            }
            recentView.adapter = CommunityFragmentRecentAdapter(recentItems)
        }

        db.collection("contents").orderBy("favoriteCount",Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            favoriteItems.clear()
            for(document in documents){
                var dto = document.toObject(ContentDTO::class.java)
                favoriteItems.add(dto)
            }
            favoriteView.adapter = CommunityFragmentRecentAdapter(favoriteItems)
        }

    }


}