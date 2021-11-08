package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jambosoft.bangbang.Adapter.HomeFragmentRecyclerAdapter
import com.jambosoft.bangbang.Adapter.KakaoMapAdapter
import com.jambosoft.bangbang.model.RoomDTO

class RoomListActivity : AppCompatActivity() {
    lateinit var db : FirebaseFirestore
    lateinit var user : FirebaseUser
    lateinit var roomItems : ArrayList<RoomDTO>
    lateinit var roomListRecyclerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)

        roomItems = arrayListOf()

        val type = intent.getStringExtra("type").toString()

        user = FirebaseAuth.getInstance().currentUser!!
        db = FirebaseFirestore.getInstance()

        //방 종류
        val roomListTextView = findViewById<TextView>(R.id.roomlist_textview)
        //뒤로가기
        val backButton = findViewById<Button>(R.id.roomlist_back_btn)
        backButton.setOnClickListener {
            finish()
        }

        roomListRecyclerView = findViewById(R.id.roomlist_recycler)
        roomListRecyclerView.layoutManager = LinearLayoutManager(this)

        if(type.equals("recommend")){
            roomListTextView.text = "인기 매물"
            setRecommendItems()
        }else if (type.equals("recent")){
            roomListTextView.text = "최근 본 방"
            setRecentItems()
        }else if(type.equals("favorite")){
            roomListTextView.text = "찜한 방"
            setFavoriteItems()
        }

    }

    fun setFavoriteItems(){
        db!!.collection("rooms").get().addOnSuccessListener { documents ->
            roomItems!!.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                if(dto.favorites.isNotEmpty()){ //favorite null체크
                    if(dto.favorites.containsKey(user!!.uid)){ //해당 key값이 있는지 체크
                        if(dto.favorites[user!!.uid]!!){ //key가 있을경우 true이면 list에 추가
                            roomItems!!.add(dto)
                        }
                    }
                }
            }
            roomListRecyclerView?.adapter = HomeFragmentRecyclerAdapter(roomItems!!)
        }
    }

    fun setRecommendItems(){
        db.collection("rooms").orderBy("favoriteCount",
            Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            roomItems.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                roomItems.add(dto)
            }

            roomListRecyclerView.adapter = KakaoMapAdapter(roomItems)
        }
    }

    fun setRecentItems(){
        db.collection("rooms").get().addOnSuccessListener { documents ->
            roomItems.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                if(dto.recents.containsKey(user.uid)){  //한번이라도 내가 본방을 다 가져옴
                    roomItems.add(dto)
                }
            }

            //방을 순서대로 정렬함
            sortRecentItems()
            roomListRecyclerView.adapter = KakaoMapAdapter(roomItems)
        }
    }

    fun sortRecentItems(){
        //var sortedRecentItems = recentItems!!.sortedWith(compareBy({it.recents[user!!.uid]}))
        var sortedRecentItems = roomItems.sortedWith(compareByDescending({it.recents[user.uid]}))

        roomItems.clear()

        for(i in 0..sortedRecentItems.size-1){
            roomItems.add(sortedRecentItems[i])
        }
    }
}