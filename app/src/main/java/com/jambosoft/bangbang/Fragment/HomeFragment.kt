package com.jambosoft.bangbang.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jambosoft.bangbang.R
import com.jambosoft.bangbang.Adapter.HomeFragmentRecentAdapter
import com.jambosoft.bangbang.Adapter.HomeFragmentRecommendAdapter
import com.jambosoft.bangbang.FilterActivity
import com.jambosoft.bangbang.UserActivity

class HomeFragment : Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView : View = inflater.inflate(R.layout.fragment_home, container, false)

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


        //추천리스트
        val recommendView = rootView.findViewById<RecyclerView>(R.id.frag_home_recommend_recyclerview)
        recommendView.adapter = HomeFragmentRecommendAdapter()
        val recommendLayoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recommendView.layoutManager = recommendLayoutManager

        //최근에 본 리스트
        val recentView = rootView.findViewById<RecyclerView>(R.id.frag_home_recent_recyclerview)
        recentView.adapter = HomeFragmentRecentAdapter()
        val recentLayoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recentView.layoutManager = recentLayoutManager



        // Inflate the layout for this fragment
        return rootView
    }



}