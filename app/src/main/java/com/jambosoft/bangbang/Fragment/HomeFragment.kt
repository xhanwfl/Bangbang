package com.jambosoft.bangbang.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.Adapter.HomeFragmentRecyclerAdapter
import com.jambosoft.bangbang.Adapter.KakaoMapAdapter
import com.jambosoft.bangbang.FilterActivity
import com.jambosoft.bangbang.RoomListActivity
import com.jambosoft.bangbang.model.RoomDTO
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    var recentItems : ArrayList<RoomDTO>? = null
    var favoriteItems : ArrayList<RoomDTO>? = null
    var user : FirebaseUser? = null
    var db : FirebaseFirestore? = null
    var recommendView : RecyclerView ? = null
    var recentView : RecyclerView ? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView : View = inflater.inflate(R.layout.fragment_home, container, false)
        user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()
        recentItems = arrayListOf()
        favoriteItems = arrayListOf()



        //쉐어하우스버튼
        val sharehouseButton = rootView.findViewById<ImageView>(R.id.frag_home_sharehouse_imageview)
        sharehouseButton.setOnClickListener {
            var intent = Intent(rootView.context,FilterActivity::class.java)
            intent.putExtra("roomkinds",true)
            startActivity(intent)
        }

        //원룸버튼
        val oneroomButton = rootView.findViewById<ImageView>(R.id.frag_home_oneroom_imageview)
        oneroomButton.setOnClickListener {
            var intent = Intent(rootView.context,FilterActivity::class.java)
            intent.putExtra("roomkinds",false)
            startActivity(intent)
        }
        
        
        //더보기 버튼
        val moreFavoriteRoomTextView = rootView.findViewById<TextView>(R.id.frag_home_more_favoriteroom_textview)
        moreFavoriteRoomTextView.setOnClickListener { 
            val intent = Intent(rootView.context,RoomListActivity::class.java)
            intent.putExtra("type","favorite")
            startActivity(intent)
        }
        val moreRecentRoomTextView = rootView.findViewById<TextView>(R.id.frag_home_more_recentroom_textview)
        moreRecentRoomTextView.setOnClickListener {
            val intent = Intent(rootView.context,RoomListActivity::class.java)
            intent.putExtra("type","recent")
            startActivity(intent)
        }

        //추천리스트
        recommendView = rootView.findViewById<RecyclerView>(R.id.frag_home_recommend_recyclerview)
        recommendView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        //최근에 본 리스트
        recentView = rootView.findViewById<RecyclerView>(R.id.frag_home_recent_recyclerview)
        recentView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        




        setRecycler()
        // Inflate the layout for this fragment
        return rootView
    }

    fun setRecycler(){
        db!!.collection("rooms").orderBy("favoriteCount",Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            favoriteItems!!.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                favoriteItems!!.add(dto)
            }

            recommendView?.adapter = HomeFragmentRecyclerAdapter(favoriteItems!!)
        }


        db!!.collection("rooms").get().addOnSuccessListener { documents ->
            recentItems!!.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                if(dto.recents.containsKey(user!!.uid)){  //한번이라도 내가 본방을 다 가져옴
                    recentItems!!.add(dto)
                }
            }

            //방을 순서대로 정렬함
            sortRecentItems()
            recentView?.adapter = HomeFragmentRecyclerAdapter(recentItems!!)
        }
    }


    fun sortRecentItems(){
        //var sortedRecentItems = recentItems!!.sortedWith(compareBy({it.recents[user!!.uid]}))
        var sortedRecentItems = recentItems!!.sortedWith(compareByDescending({it.recents[user!!.uid]}))

        recentItems!!.clear()

        for(i in 0..sortedRecentItems.size-1){
            recentItems!!.add(sortedRecentItems[i])
        }

    }

}