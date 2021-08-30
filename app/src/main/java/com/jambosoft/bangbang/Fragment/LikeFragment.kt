package com.jambosoft.bangbang.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.Adapter.KakaoMapAdapter
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.model.RoomDTO
import java.util.*
import kotlin.collections.ArrayList

class LikeFragment : Fragment() {
    var recentItems : ArrayList<RoomDTO>? = null
    var favoriteItems : ArrayList<RoomDTO>? = null
    var user : FirebaseUser? = null
    var db : FirebaseFirestore? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater.inflate(R.layout.fragment_like, container, false)
        // Inflate the layout for this fragment

        //리사이클러뷰
        val recyclerView = view.findViewById<RecyclerView>(R.id.frag_like_rooms_recycler)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        recentItems = arrayListOf()
        favoriteItems = arrayListOf()

        user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()



        //최근 본 방
        val recentButton = view.findViewById<TextView>(R.id.frag_like_recentrooms_textview)
        recentButton.setOnClickListener {
            getRecentRooms()
            recyclerView.adapter = KakaoMapAdapter(recentItems!!)
        }


        //찜한 방
        val favoriteButton = view.findViewById<TextView>(R.id.frag_like_favoriterooms_textview)
        favoriteButton.setOnClickListener {
            recyclerView.adapter = KakaoMapAdapter(favoriteItems!!)
        }


        db!!.collection("rooms").whereGreaterThan("favoriteCount",0).get().addOnSuccessListener { documents ->
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                if(dto.favorites[user!!.uid]!!){
                    favoriteItems!!.add(dto)
                }
            }

            recyclerView.adapter = KakaoMapAdapter(favoriteItems!!)
        }



        return view
    }

    fun getRecentRooms(){

        //Firestore에서 방을 모두 가져옴
        db!!.collection("rooms").get().addOnSuccessListener { documents ->
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                if(dto.recents[user!!.uid]!=null){  //한번이라도 내가 본방을 다 가져옴
                    recentItems!!.add(dto)
                }
            }


            //방을 순서대로 정렬함
            sortRecentItems()
        }
    }

    fun sortRecentItems(){
        var sortedRecentItems = recentItems!!.sortedWith(compareBy({it.recents[user!!.uid]}))

        recentItems!!.clear()

        for(i in 0..sortedRecentItems.size-1){
            recentItems!!.add(sortedRecentItems[i])
        }

    }




}