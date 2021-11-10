package com.jambosoft.bangbang.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jambosoft.bangbang.*
import com.jambosoft.bangbang.Adapter.HomeFragmentRecyclerAdapter
import com.jambosoft.bangbang.Adapter.KakaoMapAdapter
import com.jambosoft.bangbang.model.RoomDTO
import com.jambosoft.bangbang.model.RoomLocationInfoDTO
import kotlinx.coroutines.*

class HomeFragment : Fragment() {
    var recentItems : ArrayList<RoomDTO>? = null
    var recommendItems : ArrayList<RoomDTO>? = null
    var favoriteItems : ArrayList<RoomDTO>? = null
    var user : FirebaseUser? = null
    var db : FirebaseFirestore? = null
    var recommendView : RecyclerView ? = null
    var recentView : RecyclerView ? = null
    var favoriteView : RecyclerView ? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView : View = inflater.inflate(R.layout.fragment_home, container, false)
        user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance()
        recentItems = arrayListOf()
        favoriteItems = arrayListOf()
        recommendItems = arrayListOf()

        //지도로 찾기
        val openMapLayout = rootView.findViewById<LinearLayout>(R.id.frag_home_openmap_layout)
        openMapLayout.setOnClickListener{
            openMap()
        }
        val openMapImageView = rootView.findViewById<ImageView>(R.id.frag_home_openmap_imageview)
        openMapImageView.setOnClickListener {
            openMap()
        }

        //지하철역으로 찾기
        val findSubwayLayout = rootView.findViewById<LinearLayout>(R.id.frag_home_findsubway_layout)
        findSubwayLayout.setOnClickListener {
            startSearchActivity()
        }
        val findSubwayImageView = rootView.findViewById<ImageView>(R.id.frag_home_findsubway_imageview)
        findSubwayImageView.setOnClickListener {
            startSearchActivity()
        }

/*
        //쉐어하우스버튼
        val sharehouseButton = rootView.findViewById<ImageView>(R.id.frag_home_sharehouse_imageview)
        sharehouseButton.setOnClickListener {
            val intent = Intent(rootView.context,KakaoMapActivity::class.java)
            intent.putExtra("roomkinds",1)
            startActivity(intent)
        }

        //원룸버튼
        val oneroomButton = rootView.findViewById<ImageView>(R.id.frag_home_oneroom_imageview)
        oneroomButton.setOnClickListener {
            val intent = Intent(rootView.context,KakaoMapActivity::class.java)
            intent.putExtra("roomkinds",2)
            startActivity(intent)
        }
        */
        
        //더보기 버튼
        val moreRecommendRoomTextView = rootView.findViewById<TextView>(R.id.frag_home_more_recommendroom_textview)
        moreRecommendRoomTextView.setOnClickListener {
            val intent = Intent(rootView.context,RoomListActivity::class.java)
            intent.putExtra("type","recommend")
            startActivity(intent)
        }
        val moreRecentRoomTextView = rootView.findViewById<TextView>(R.id.frag_home_more_recentroom_textview)
        moreRecentRoomTextView.setOnClickListener {
            val intent = Intent(rootView.context,RoomListActivity::class.java)
            intent.putExtra("type","recent")
            startActivity(intent)
        }
        val moreFavoriteRoomTextView = rootView.findViewById<TextView>(R.id.frag_home_more_favoriteroom_textview)
        moreFavoriteRoomTextView.setOnClickListener {
            val intent = Intent(rootView.context,RoomListActivity::class.java)
            intent.putExtra("type","favorite")
            startActivity(intent)
        }

        //추천리스트
        recommendView = rootView.findViewById(R.id.frag_home_recommend_recyclerview)
        recommendView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        //최근에 본 리스트
        recentView = rootView.findViewById(R.id.frag_home_recent_recyclerview)
        recentView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        //찜한 방
        favoriteView = rootView.findViewById(R.id.frag_home_favorite_recyclerview)
        favoriteView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)






        setRecycler()
        // Inflate the layout for this fragment
        return rootView
    }

    fun startSearchActivity(){
        startActivityForResult(Intent(context, SearchActivity::class.java),300)
    }

    fun openMap(){
        val intent = Intent(requireContext(),KakaoMapActivity::class.java)
        //지도버튼 클릭 구분
        intent.putExtra("btnKinds",0)
        startActivity(intent)
    }

    fun setRecycler(){
        db!!.collection("rooms").orderBy("favoriteCount",Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            recommendItems!!.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                recommendItems!!.add(dto)
            }

            recommendView?.adapter = HomeFragmentRecyclerAdapter(recommendItems!!)
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

        db!!.collection("rooms").get().addOnSuccessListener { documents ->
            favoriteItems!!.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                if(dto.favorites.isNotEmpty()){ //favorite null체크
                    if(dto.favorites.containsKey(user!!.uid)){ //해당 key값이 있는지 체크
                        if(dto.favorites[user!!.uid]!!){ //key가 있을경우 true이면 list에 추가
                            favoriteItems!!.add(dto)
                        }
                    }
                }
            }
            favoriteView?.adapter = HomeFragmentRecyclerAdapter(favoriteItems!!)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==AppCompatActivity.RESULT_OK&&requestCode==300){
            val roomLocationInfoDTO = data?.getSerializableExtra("dto") as RoomLocationInfoDTO
            val intent = Intent(context, KakaoMapActivity::class.java)
            intent.putExtra("latitude",roomLocationInfoDTO.latitude)
            intent.putExtra("longitude",roomLocationInfoDTO.longitude)
            intent.putExtra("btnKinds",1)
            startActivity(intent)
        }
    }


}