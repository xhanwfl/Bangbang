package com.jambosoft.bangbang

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.Adapter.DetailRoomImageAdapter
import com.jambosoft.bangbang.Adapter.KakaoMapAdapter
import com.jambosoft.bangbang.Adapter.SearchContentAdapter
import com.jambosoft.bangbang.Network.KakaoSearchAPI
import com.jambosoft.bangbang.model.ResultSearchKeyword
import com.jambosoft.bangbang.model.RoomDTO
import com.jambosoft.bangbang.model.RoomLocationInfoDTO
import com.jambosoft.bangbang.model.SearchContentDTO
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapView

import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class KakaoMapActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    var latitude : Double = 0.0
    var longitude : Double = 0.0
    var depositFee : Int = 0
    var monthlyFee : Int = 0
    var roomkinds : Int = 0
    var roomDTOList : ArrayList<RoomDTO>? = null
    var roomDTOs : ArrayList<RoomDTO>? = null
    var mapView : MapView? = null
    var mapViewContainer : ViewGroup? = null
    var recyclerView : RecyclerView? = null
    var isMarkerClicked = false
    var isFirstClicked = false
    var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    val myLocMarkerTag = 1000
    var isFilterVisible = false
    var isSearchVisible = false
    lateinit var filterLayout : LinearLayout
    lateinit var allTextView: TextView
    lateinit var sharehouseTextView: TextView
    lateinit var oneroomTextView: TextView
    private val listItems = arrayListOf<SearchContentDTO>()
    private val listAdapter = SearchContentAdapter(listItems)
    private var keyword = ""
    lateinit var searchLayout : LinearLayout
    init{
        roomDTOList = arrayListOf()
        roomDTOs = arrayListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_map)
        //맵뷰 초기화
        mapView = MapView(this)
        mapViewContainer = findViewById(R.id.map_view)

        latitude = intent.getStringExtra("latitude")!!.toDouble()
        longitude = intent.getStringExtra("longitude")!!.toDouble()
        depositFee = intent.getIntExtra("depositFee",1000000) //default를 전체보기로 해놓음
        monthlyFee = intent.getIntExtra("monthlyFee",1000000)
        roomkinds = intent.getIntExtra("roomkinds", 0)

        recyclerView = findViewById(R.id.kakaomap_content_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        Log.e("wow","latitude : ${latitude}\nlongitude : ${longitude}\ndepositFee : ${depositFee}\nmonthlyFee : ${monthlyFee}\nroomkinds : ${roomkinds}\n")

        //마커 이벤트 리스너
        //val myPOIItemEventListener = MyPOIItemEventListener()

        //맵뷰 이벤트 리스너
        //val myMapViewEventListener = MyMapViewEventListener()


        //검색
        val searchRecyclerView = findViewById<RecyclerView>(R.id.kakaomap_search_recycler)
        val searchEditText = findViewById<EditText>(R.id.kakaomap_search_edittext)
        searchLayout = findViewById<LinearLayout>(R.id.kakaomap_search_layout)
        val searchCancelTextView = findViewById<TextView>(R.id.kakaomap_search_cancel_textview)
        val searchButton = findViewById<Button>(R.id.kakaomap_search_btn)
        searchButton.setOnClickListener {
            if(!isSearchVisible){
                searchLayout.visibility = View.VISIBLE
                isSearchVisible = true

                //EditText에 focus넣기
                searchEditText.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)


            }
        }

        //검색 취소버튼
        searchCancelTextView.setOnClickListener {
            if(isSearchVisible){
                searchLayout.visibility = View.GONE
                isSearchVisible = false
            }
        }

        //검색기능 확인버튼 클릭리스너
        searchEditText.imeOptions = EditorInfo.IME_ACTION_DONE  //키보드 다음버튼을 확인으로 바꿔줌
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                keyword = searchEditText.text.toString()
                searchKeyword(keyword)

            }

            false
        }

        //검색목록 recycler
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.adapter = listAdapter





        //필터
        val filterButton = findViewById<Button>(R.id.kakaomap_filter_btn)
        val filterButton2 = findViewById<Button>(R.id.kakaomap_filter_btn2)
        filterLayout = findViewById(R.id.kakaomap_filter_layout)
        filterButton.setOnClickListener {
            if(!isFilterVisible){
                filterLayout.visibility = View.VISIBLE
                isFilterVisible = true
            }else{
                filterLayout.visibility = View.GONE
                isFilterVisible = false
            }
        }
        filterButton2.setOnClickListener {
            if(!isFilterVisible){
                filterLayout.visibility = View.VISIBLE
                isFilterVisible = true
            }else{
                filterLayout.visibility = View.GONE
                isFilterVisible = false
            }
        }


        //방 종류
        allTextView = findViewById(R.id.kakaomap_filter_all_textview)
        sharehouseTextView = findViewById(R.id.kakaomap_filter_sharehouse_textview)
        oneroomTextView = findViewById(R.id.kakaomap_filter_oneroom_textview)

        //모두보기
        allTextView.setOnClickListener {
            setRoomKindBackgroundColor(0)
            roomkinds = 0
        }
        //쉐어하우스
        sharehouseTextView.setOnClickListener {
            setRoomKindBackgroundColor(1)
            roomkinds = 1
        }
        //원룸
        oneroomTextView.setOnClickListener {
            setRoomKindBackgroundColor(2)
            roomkinds = 2
        }


        //적용하기 버튼
        val applyButton = findViewById<Button>(R.id.kakaomap_filter_apply_btn)
        applyButton.setOnClickListener {
            applyFilter()
        }


        //보증금 seekBar
        val text = findViewById<TextView>(R.id.kakaomap_filter_seekbar_textview)
        val seekBar = findViewById<SeekBar>(R.id.kakaomap_filter_seekbar)
        seekBar.setProgress(1001,true)
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress<100){
                    text.text = "없음"
                } else if(progress<=1000){
                    depositFee = progress-(progress%100)
                    text.text = "${depositFee}만원 이하"
                } else {
                    depositFee = 100000
                    text.text = "전체보기"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        //월세 seekBar
        val text2 = findViewById<TextView>(R.id.kakaomap_filter_seekbar2_textview)
        val seekBar2 = findViewById<SeekBar>(R.id.kakaomap_filter_seekbar2)
        seekBar2.setProgress(1001,true)
        seekBar2.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress<=100){
                    monthlyFee = progress
                    text2.text = "${monthlyFee}만원 이하"
                }else{
                    monthlyFee = 10000
                    text2.text = "전체보기"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })



        mapView?.apply {
            //중심점변경 + 줌레벨 변경
            mapView?.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude,longitude),4,true)

            //줌 인
            zoomIn(true)
            //줌 아웃
            zoomOut(true)
            //마커 이벤트 리스너
            setPOIItemEventListener(this@KakaoMapActivity)
            //맵뷰 이벤트 리스너
            setMapViewEventListener(this@KakaoMapActivity)
            //커스텀 말풍선 등록
            //setCalloutBalloonAdapter(CustomBalloonAdapter())
        }

        //내위치 버튼
        val myLocMarker = MapPOIItem()
        val myLocationButton = findViewById<Button>(R.id.kakaomap_mylocation_btn)
        myLocationButton.setOnClickListener {
            val loc = getCurrentLatLng()
            if(loc!=null){
                val myMapPoint = MapPoint.mapPointWithGeoCoord(loc.latitude,loc.longitude)
                mapView?.setMapCenterPoint(myMapPoint,true)
                myLocMarker.apply {
                    mapPoint = myMapPoint
                    itemName = "내 위치"
                    tag = myLocMarkerTag //마커 구분하는 태그값
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                mapView?.addPOIItem(myLocMarker)
            }
        }



        //방 정보 가져와서 roomDTOList에 추가
        getRooms()

        mapViewContainer?.addView(mapView)

    }

    //검색기능 이너클래스
    inner class SearchContentAdapter(val itemList: ArrayList<SearchContentDTO>): RecyclerView.Adapter<SearchContentAdapter.ViewHolder>() {
        var context : Activity? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchContentAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_content, parent, false)
            this.context = parent.context as Activity
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        override fun onBindViewHolder(holder: SearchContentAdapter.ViewHolder, position: Int) {
            holder.name.text = itemList[position].name
            holder.road.text = itemList[position].road
            holder.address.text = itemList[position].address
            // 아이템 클릭 이벤트
            holder.itemView.setOnClickListener {
                Log.e("clicked","${position}+번째 아이템")
                val mapPoint = MapPoint.mapPointWithGeoCoord(itemList[position].y.toDouble(),itemList[position].x.toDouble())
                mapView?.setMapCenterPoint(mapPoint,true)
                searchLayout.visibility=View.GONE
                isSearchVisible = false

            }
        }

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.tv_list_name)
            val road: TextView = itemView.findViewById(R.id.tv_list_road)
            val address: TextView = itemView.findViewById(R.id.tv_list_address)
        }
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(SearchActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoSearchAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(SearchActivity.API_KEY, keyword)   // 검색 조건 입력

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

    //필터 적용
    fun applyFilter(){
        getRooms()
        filterLayout.visibility=View.GONE
    }
    //필터 방종류 구분함수
    fun setRoomKindBackgroundColor(position : Int){
        allTextView.setBackgroundColor(this.resources.getColor(R.color.white))
        sharehouseTextView.setBackgroundColor(this.resources.getColor(R.color.white))
        oneroomTextView.setBackgroundColor(this.resources.getColor(R.color.white))
        when(position){
            0 ->{
                allTextView.setBackgroundColor(this.resources.getColor(R.color.gray))
            }
            1 ->{
                sharehouseTextView.setBackgroundColor(this.resources.getColor(R.color.gray))
            }
            2 ->{
                oneroomTextView.setBackgroundColor(this.resources.getColor(R.color.gray))
            }
        }
    }


    //현재위치 얻기
    private fun getCurrentLatLng(): Location? {
        var currentLatLng: Location? = null
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locatioNProvider = LocationManager.NETWORK_PROVIDER
        val hasFineLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            currentLatLng = locationManager.getLastKnownLocation(locatioNProvider)
            return currentLatLng!!
        }

        return currentLatLng
    }

    fun setMarkers(){
        //마커 추가
        mapView?.removeAllPOIItems()
        for(i in 0..roomDTOList!!.size-1){
            var marker = MapPOIItem()
            marker?.apply {
                itemName = "${roomDTOList!![i].deposit}/${roomDTOList!![i].monthlyFee}" // 마커 이름
                marker.tag = i
                mapPoint = MapPoint.mapPointWithGeoCoord(roomDTOList!![i].address.latitude.toDouble()//마커 좌표
                        ,roomDTOList!![i].address.longitude.toDouble())
                Log.e("lati",roomDTOList!![i].address.latitude+"\t"+roomDTOList!![i].address.longitude)
                /*markerType = MapPOIItem.MarkerType.BluePin
                selectedMarkerType = MapPOIItem.MarkerType.RedPin*/
                markerType = MapPOIItem.MarkerType.CustomImage
                customImageResourceId = R.drawable.ic_marker
                selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                customSelectedImageResourceId = R.drawable.ic_marker_selected
                isCustomImageAutoscale = false
                setCustomImageAnchor(0.5f, 1.0f)
                }
            mapView?.addPOIItem(marker)
        }
        setRecyclerAdapter()
    }

    fun setRecyclerAdapter(){
        recyclerView?.adapter = adapter
    }

    fun getRooms(){
        val db =FirebaseFirestore.getInstance()

        when(roomkinds){
            0 ->{ //모두보기
                db.collection("rooms").get().addOnSuccessListener { documents ->
                    roomDTOList?.clear()
                    for(document in documents){
                        val dto = document.toObject(RoomDTO::class.java)
                        if(dto.deposit<=depositFee&&dto.monthlyFee<=monthlyFee){
                            roomDTOList?.add(dto)
                        }
                    }
                    setMarkers()
                }
            }
            1 ->{ //쉐어하우스
                db.collection("rooms").whereEqualTo("roomKinds",true).get().addOnSuccessListener { documents ->
                    roomDTOList?.clear()
                    for(document in documents){
                        val dto = document.toObject(RoomDTO::class.java)
                        if(dto.deposit<=depositFee&&dto.monthlyFee<=monthlyFee){
                            roomDTOList?.add(dto)
                        }
                    }
                    setMarkers()
                }
            }
            2 ->{ //원룸
                db.collection("rooms").whereEqualTo("roomKinds",false).get().addOnSuccessListener { documents ->
                    roomDTOList?.clear()
                    for(document in documents){
                        val dto = document.toObject(RoomDTO::class.java)
                        if(dto.deposit<=depositFee&&dto.monthlyFee<=monthlyFee){
                            roomDTOList?.add(dto)
                        }
                    }
                    setMarkers()
                }
            }
        }
    }


    //커스텀 말풍선 클래스
    class CustomBalloonAdapter : CalloutBalloonAdapter{
        override fun getCalloutBalloon(p0: MapPOIItem?) : View?{
            //마커 클릭시 나오는 말풍선
            return null
        }

        override fun getPressedCalloutBalloon(p0: MapPOIItem?): View? {
            //말풍선 클릭 시
            return null
        }

    }

    //mapViewEventListener
    override fun onMapViewInitialized(p0: MapView?) {
       Log.e("감지","맵뷰리스너 초기화")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        Log.e("감지","줌레벨 ${p1}")
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        if(isMarkerClicked){
            isMarkerClicked = false
            recyclerView?.visibility = View.GONE
        }
        if(isFilterVisible){
            isFilterVisible = false
            filterLayout.visibility = View.GONE
        }
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }


    //마커 클릭이벤트 리스너
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

        if (p1 != null) {
            if(p1.tag==myLocMarkerTag){ //내위치 마커일경우

            }else{ //내위치 마커가 아닐경우
                roomDTOList!![p1!!.tag]

                if(!isMarkerClicked) { //마커클릭시 recyclerview 초기화
                    isMarkerClicked = true
                    Log.e("감지","마커클릭이벤트 ${isMarkerClicked}")
                    roomDTOs?.clear()
                    roomDTOs?.add(roomDTOList!![p1.tag])

                    mapView?.setMapCenterPoint(p1.mapPoint,true)
                    if(!isFirstClicked){ //초기화는 맨처음 한번만
                        adapter = KakaoMapAdapter(roomDTOs!!)
                        setRecyclerAdapter()
                        isFirstClicked = true
                    }else{
                        adapter?.notifyDataSetChanged()
                    }
                    recyclerView?.visibility = View.VISIBLE

                }else if(isMarkerClicked){ //마커를 다시 클릭시 recyclerview 끄기
                    isMarkerClicked = false
                    recyclerView?.visibility = View.GONE
                }
            }
        }


    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //필터
        if(resultCode==RESULT_OK&&requestCode==300){
            depositFee = intent.getIntExtra("depositFee",1000000) //default를 전체보기로 해놓음
            monthlyFee = intent.getIntExtra("monthlyFee",1000000)
            roomkinds = intent.getIntExtra("roomkinds", 0)
            Log.e("!sibal","roomKinds : $roomkinds    depositFee : $depositFee")
            getRooms()
        }
    }

}