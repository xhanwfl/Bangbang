package com.jambosoft.bangbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jambosoft.bangbang.Adapter.SearchContentAdapter
import com.jambosoft.bangbang.Network.KakaoSearchAPI
import com.jambosoft.bangbang.model.ResultSearchKeyword
import com.jambosoft.bangbang.model.SearchContentDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 9caad1fd8e5a7e2b755af618aca29901"  // REST API 키
    }

    private val listItems = arrayListOf<SearchContentDTO>()   // 리사이클러 뷰 아이템
    private val listAdapter = SearchContentAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var keyword = ""        // 검색 키워드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var recyclerView = findViewById<RecyclerView>(R.id.search_recycler)
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)



        val searchButton = findViewById<Button>(R.id.search_search_btn)
        searchButton.setOnClickListener {
            val searchEditText = findViewById<EditText>(R.id.search_search_edittext)
            keyword = searchEditText.text.toString()
            searchKeyword(keyword)
        }
    }







    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoSearchAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                addRecyclerItems(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.e("SearchActivity", "통신 실패: ${t.message}")
            }
        })
    }

    private fun addRecyclerItems(searchResult : ResultSearchKeyword?){
        if(!searchResult?.documents.isNullOrEmpty()){
            //검색결과 있음
            listItems.clear() //리스트 초기화
            for(document in searchResult!!.documents){
                //결과를 리사이클러 뷰에 추가
                val item = SearchContentDTO(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x,
                    document.y)
                listItems.add(item)
            }

            listAdapter.notifyDataSetChanged()
        }
    }
}