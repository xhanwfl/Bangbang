package com.jambosoft.bangbang.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jambosoft.bangbang.Adapter.CommunityFragmentContentAdapter
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
            val intent = Intent(rootView.context,WriteContentActivity::class.java)
            intent.putExtra("action","write")
            startActivity(intent)
        }

        //최신글, 인기글
        val recentTextView = rootView.findViewById<TextView>(R.id.frag_community_recent_textview)
        val favoriteTextView = rootView.findViewById<TextView>(R.id.frag_community_favorite_textview)
        recentTextView.setOnClickListener {
            setContent()
            favoriteTextView.setBackgroundColor(requireContext().resources.getColor(R.color.white))
            recentTextView.setBackgroundColor(requireContext().resources.getColor(R.color.gray))
            favoriteView.visibility = View.GONE
            recentView.visibility = View.VISIBLE
        }

        favoriteTextView.setOnClickListener {
            setContent()
            favoriteTextView.setBackgroundColor(requireContext().resources.getColor(R.color.gray))
            recentTextView.setBackgroundColor(requireContext().resources.getColor(R.color.white))
            recentView.visibility = View.GONE
            favoriteView.visibility = View.VISIBLE
        }



        setContent()


        return rootView
    }

    override fun onResume() {
        super.onResume()

        setContent() //삭제 후 리스트초기화
    }


    fun setContent(){
        db.collection("contents").orderBy("timestamp",Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            recentItems.clear()
            for(document in documents){
                var dto = document.toObject(ContentDTO::class.java)
                recentItems.add(dto)
            }
            recentView.adapter = CommunityFragmentContentAdapter(recentItems)

        }

        db.collection("contents").orderBy("favoriteCount",Query.Direction.DESCENDING).get().addOnSuccessListener { documents ->
            favoriteItems.clear()
            for(document in documents){
                var dto = document.toObject(ContentDTO::class.java)
                favoriteItems.add(dto)
            }
            favoriteView.adapter = CommunityFragmentContentAdapter(favoriteItems)
        }

    }


}