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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapView

import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.hypot
import kotlin.properties.Delegates


class KakaoMapActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    var latitude : Double = 37.53737528
    var longitude : Double = 127.00557633
    var maxDepositFee : Int = 1000000
    var minDepositFee : Int = 0
    var maxMonthlyFee : Int = 1000000
    var minMonthlyFee : Int = 0
    var minAdminFee : Int = 0
    var maxAdminFee : Int = 1000
    var roomkinds : Int = -1
    var roomDTOList : ArrayList<RoomDTO> = arrayListOf()
    var roomDTOs : ArrayList<RoomDTO> = arrayListOf()
    var contractType : Int = 0
    var clusterList : ArrayList<RoomDTO> = arrayListOf()
    var currentItems : ArrayList<RoomDTO> = arrayListOf()
    var floorNum : Int = -1
    var mapView : MapView? = null
    var mapViewContainer : ViewGroup? = null
    var recyclerView : RecyclerView? = null
    var isMarkerClicked = false
    var isFirstClicked = false
    var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    val myLocMarkerTag = 1000
    val clusterTag = 2000
    var isFilterVisible = false
    var isSearchVisible = false
    var isClusterVisible = false
    var clusterCount = 3
    val positionCluster : ArrayList<Int> = arrayListOf()
    val floorNumArr : Array<Int> = arrayOf(1,1,1,1,1,1)
    val flooNumAll : Int = 1
    lateinit var filterLayout : LinearLayout
    lateinit var allTextView: TextView
    lateinit var sharehouseTextView: TextView
    lateinit var oneroomTextView: TextView
    lateinit var floorNumAllButton : Button
    lateinit var floorNumOneButton : Button
    lateinit var floorNumTwoButton : Button
    lateinit var floorNumThreeButton : Button
    lateinit var floorNumFourButton : Button
    lateinit var floorNumFiveButton : Button
    lateinit var floorNumUndergroundButton : Button
    lateinit var slide_up : Animation
    lateinit var slide_down : Animation
    private val listItems = arrayListOf<SearchContentDTO>()
    private val listAdapter = SearchContentAdapter(listItems)
    private var keyword = ""
    lateinit var searchLayout : LinearLayout
    lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_map)
        db = FirebaseFirestore.getInstance()

        //?????? ?????????
        mapView = MapView(this)
        mapViewContainer = findViewById(R.id.map_view)


        val btnKinds = intent.getIntExtra("btnKinds", 0)

        if(btnKinds == 0){ // ?????????????????? ???????????????
            val loc = getCurrentLatLng()  //???????????? ????????????
            if(loc!=null){  //null?????? ??? ????????????
                latitude = loc.latitude
                longitude = loc.longitude
            }
        }else{ //????????????????????? ???????????????
            latitude = intent.getStringExtra("latitude")!!.toDouble()
            longitude = intent.getStringExtra("longitude")!!.toDouble()
        }

        recyclerView = findViewById(R.id.kakaomap_content_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(this)


        //????????????
        slide_down = AnimationUtils.loadAnimation(applicationContext,R.anim.slide_down)
        slide_up = AnimationUtils.loadAnimation(applicationContext,R.anim.slide_up)

        //?????? ????????? ?????????
        //val myPOIItemEventListener = MyPOIItemEventListener()

        //?????? ????????? ?????????
        //val myMapViewEventListener = MyMapViewEventListener()



        //???????????? ??????
        val backButton = findViewById<Button>(R.id.kakaomap_back_btn)
        backButton.setOnClickListener {
            finish()
        }


        //??????
        val searchRecyclerView = findViewById<RecyclerView>(R.id.kakaomap_search_recycler)
        val searchEditText = findViewById<EditText>(R.id.kakaomap_search_edittext)
        searchLayout = findViewById(R.id.kakaomap_search_layout)
        val searchCancelTextView = findViewById<TextView>(R.id.kakaomap_search_cancel_textview)
        val searchButton = findViewById<Button>(R.id.kakaomap_search_btn)
        searchButton.setOnClickListener {
            if(!isSearchVisible){
                searchLayout.visibility = View.VISIBLE
                isSearchVisible = true

                //EditText??? focus??????
                searchEditText.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }




        //?????? ????????????
        searchCancelTextView.setOnClickListener {
            if(isSearchVisible){
                searchLayout.visibility = View.GONE
                isSearchVisible = false
            }
        }

        //???????????? ???????????? ???????????????
        searchEditText.imeOptions = EditorInfo.IME_ACTION_DONE  //????????? ??????????????? ???????????? ?????????
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                keyword = searchEditText.text.toString()
                searchKeyword(keyword)
            }
            false
        }

        //???????????? recycler
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.adapter = listAdapter

        //??????

        val filterButton = findViewById<Button>(R.id.kakaomap_filter_btn)
        val filterButton2 = findViewById<Button>(R.id.kakaomap_filter_btn2)
        filterLayout = findViewById(R.id.kakaomap_filter_layout)
        filterButton.setOnClickListener {
            showOrHideFilterLayout()
        }
        filterButton2.setOnClickListener {
            showOrHideFilterLayout()
        }

        //???????????? ??????
        val applyButton = findViewById<Button>(R.id.kakaomap_filter_apply_btn)
        applyButton.setOnClickListener {
            applyFilter()
        }

        //????????? ?????? (?????????)
        val roomKindRadioGroup = findViewById<RadioGroup>(R.id.kakaomap_roomkind_radioGroup)
        roomKindRadioGroup.setOnCheckedChangeListener{ radioGroup, i ->
            when(i){
                R.id.kakaomap_oneroom_radiobtn ->{
                    roomkinds = 0
                }
                R.id.kakaomap_tworoom_radiobtn ->{
                    roomkinds = 1
                }
                R.id.kakaomap_officetel_radiobtn ->{
                    roomkinds = 2
                }
                R.id.kakaomap_sharehouse_radiobtn ->{
                    roomkinds = 3
                }
            }
        }

        //????????? ?????? (???,??????)
        val monthlyFeeLayout = findViewById<ConstraintLayout>(R.id.kakaomap_monthlyfee_layout)
        val contractTypeRadioGroup = findViewById<RadioGroup>(R.id.kakaomap_contracttype_radioGroup)
        contractTypeRadioGroup.setOnCheckedChangeListener{ radioGroup, i ->
            when(i){
                R.id.kakaomap_monthlyfee_radiobtn ->{  //??????
                    contractType = 0
                    monthlyFeeLayout.visibility = View.VISIBLE
                }
                R.id.kakaomap_charter_radiobtn -> {  //??????
                    contractType = 1
                    monthlyFeeLayout.visibility = View.INVISIBLE
                }
            }
        }


        //????????? seekBar
        val depositMinTextView = findViewById<TextView>(R.id.kakaomap_filter_seekbar_min_textview)
        val depositMaxTextView = findViewById<TextView>(R.id.kakaomap_filter_seekbar_max_textview)
        val seekBar = findViewById<RangeSeekBar<Int>>(R.id.kakaomap_filter_seekbar)
        seekBar.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            minDepositFee = calculateSeekBarValue(minValue)
            when(minDepositFee){
                10000 ->{
                    depositMinTextView.text = "1??????"
                }
                100000 ->{
                    depositMinTextView.text = "10??????"
                }
                else ->{
                    depositMinTextView.text = "${minDepositFee}??????"
                }
            }
            maxDepositFee = calculateSeekBarValue(maxValue)
            when(maxDepositFee){
                10000 ->{
                    depositMaxTextView.text = "1??????"
                }
                100000 ->{
                    depositMaxTextView.text = "~"
                }
                else ->{
                    depositMaxTextView.text = "${maxDepositFee}??????"
                }
            }
        }

        //?????? seekBar
        val monthlyMinTextView = findViewById<TextView>(R.id.kakaomap_filter_seekbar2_min_textview)
        val monthlyMaxTextView = findViewById<TextView>(R.id.kakaomap_filter_seekbar2_max_textview)
        val seekBar2 = findViewById<RangeSeekBar<Int>>(R.id.kakaomap_filter_seekbar2)

        seekBar2.setOnRangeSeekBarChangeListener{ bar, minValue, maxValue ->
            monthlyMinTextView.text = "${minValue}??????"
            minMonthlyFee = minValue
            if(maxValue<100){
                monthlyMaxTextView.text = "${maxValue}??????"
                maxMonthlyFee = maxValue
            }else if(maxValue<104){
                monthlyMaxTextView.text = "100??????"
                maxMonthlyFee = 100
            }else{
                monthlyMaxTextView.text = "~"
                maxMonthlyFee = 10000
            }
        }

        //?????? seekBar
        val adminFeeMinTextView = findViewById<TextView>(R.id.kakaomap_filter_seekbar3_min_textview)
        val adminFeeMaxTextView = findViewById<TextView>(R.id.kakaomap_filter_seekbar3_max_textview)
        val seekBar3 = findViewById<RangeSeekBar<Int>>(R.id.kakaomap_filter_seekbar3)

        seekBar3.setOnRangeSeekBarChangeListener{ bar, minValue, maxValue ->
            adminFeeMinTextView.text = "${minValue}??????"
            minAdminFee = minValue
            if(maxValue<40){
                adminFeeMaxTextView.text = "${maxValue}??????"
                maxAdminFee = maxValue
            }else if(maxValue<45){
                adminFeeMaxTextView.text = "40??????"
                maxAdminFee = 40
            }else{
                adminFeeMaxTextView.text = "~"
                maxAdminFee = 10000
            }
        }



        //?????? button
        floorNumAllButton = findViewById(R.id.kakaomap_floornum_all_btn)
        floorNumUndergroundButton = findViewById(R.id.kakaomap_floornum_underground_btn)
        floorNumOneButton = findViewById(R.id.kakaomap_floornum_one_btn)
        floorNumTwoButton = findViewById(R.id.kakaomap_floornum_two_btn)
        floorNumThreeButton = findViewById(R.id.kakaomap_floornum_three_btn)
        floorNumFourButton = findViewById(R.id.kakaomap_floornum_four_btn)
        floorNumFiveButton = findViewById(R.id.kakaomap_floornum_five_btn)

        //?????????
        floorNumAllButton.isSelected = true
        floorNumOneButton.isSelected = true
        floorNumTwoButton.isSelected = true
        floorNumThreeButton.isSelected = true
        floorNumFourButton.isSelected = true
        floorNumFiveButton.isSelected = true
        floorNumUndergroundButton.isSelected = true

        //???????????????
        floorNumAllButton.setOnClickListener {
            checkViewSelected(it,-1)
        }
        floorNumUndergroundButton.setOnClickListener {
            checkViewSelected(it,0)
        }
        floorNumOneButton.setOnClickListener {
            checkViewSelected(it,1)
        }
        floorNumTwoButton.setOnClickListener {
            checkViewSelected(it,2)
        }
        floorNumThreeButton.setOnClickListener {
            checkViewSelected(it,3)
        }
        floorNumFourButton.setOnClickListener {
            checkViewSelected(it,4)
        }
        floorNumFiveButton.setOnClickListener {
            checkViewSelected(it,5)
        }



        mapView?.apply {
            //??????????????? + ????????? ??????

            mapView?.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude,longitude),4,true)

            //??? ???
            zoomIn(true)
            //??? ??????
            zoomOut(true)
            //?????? ????????? ?????????
            setPOIItemEventListener(this@KakaoMapActivity)
            //?????? ????????? ?????????
            setMapViewEventListener(this@KakaoMapActivity)
            //????????? ????????? ??????
            //setCalloutBalloonAdapter(CustomBalloonAdapter())
        }

        //????????? ??????
        val myLocMarker = MapPOIItem()
        val myLocationButton = findViewById<Button>(R.id.kakaomap_mylocation_btn)
        myLocationButton.setOnClickListener {
            val loc = getCurrentLatLng()
            if(loc!=null){
                val myMapPoint = MapPoint.mapPointWithGeoCoord(loc.latitude,loc.longitude)
                mapView?.setMapCenterPoint(myMapPoint,true)
                myLocMarker.apply {
                    mapPoint = myMapPoint
                    itemName = "??? ??????"
                    tag = myLocMarkerTag //?????? ???????????? ?????????
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                mapView?.addPOIItem(myLocMarker)
            }
        }

        //?????? , ????????????
        val zoomInButton = findViewById<Button>(R.id.kakaomap_plus_btn)
        zoomInButton.setOnClickListener {
            mapView?.zoomIn(true)
        }
        val zoomOutButton = findViewById<Button>(R.id.kakaomap_minus_btn)
        zoomOutButton.setOnClickListener {
            mapView?.zoomOut(true)
        }

        //??? ?????? ???????????? roomDTOList??? ??????
        getRooms()

        mapViewContainer?.addView(mapView)

    }

    fun showOrHideFilterLayout(){
        if(!isFilterVisible){
            filterLayout.visibility = View.VISIBLE
            filterLayout.startAnimation(slide_up)
            isFilterVisible = true
            filterLayout.isClickable = true
            recyclerView?.visibility = View.GONE
        }else{
            filterLayout.visibility = View.INVISIBLE
            filterLayout.startAnimation(slide_down)
            isFilterVisible = false
            filterLayout.isClickable = false
        }
    }

    fun checkViewSelected(view : View, num : Int){
        view.isSelected = !view.isSelected

        if(view==floorNumAllButton){ //?????? ?????? ?????????
            if(view.isSelected){ //????????????
                floorNumOneButton.isSelected = true
                floorNumTwoButton.isSelected = true
                floorNumThreeButton.isSelected = true
                floorNumFourButton.isSelected = true
                floorNumFiveButton.isSelected = true
                floorNumUndergroundButton.isSelected = true
                for(i in floorNumArr.indices){
                    floorNumArr[i]=1
                }
            }else{ //????????????
                floorNumOneButton.isSelected = false
                floorNumTwoButton.isSelected = false
                floorNumThreeButton.isSelected = false
                floorNumFourButton.isSelected = false
                floorNumFiveButton.isSelected = false
                floorNumUndergroundButton.isSelected = false
                for(i in floorNumArr.indices){
                    floorNumArr[i]=0
                }
            }
        }else{ //??????????????? ????????????
            if(view.isSelected){
                floorNumArr[num] = 1
            }else{
                floorNumArr[num] = 0
            }

            //???????????? ??????
            var isAllChecked = true
            for(i in floorNumArr.indices){
                if(floorNumArr[i]==0){
                    isAllChecked = false
                }
            }
            floorNumAllButton.isSelected = isAllChecked
        }
    }

    fun calculateSeekBarValue(value : Int) : Int{
        var result = 0
        if(value<5000){
            result = value/5 - (value/5)%100
        }else if(value<9000){
            result = ((value-5000)/500 + 1)*1000
        }else if(value<10400){
            result = 10000
        }else{
            result = 100000
        }

        return result
    }


    //???????????? ???????????????
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
            // ????????? ?????? ?????????
            holder.itemView.setOnClickListener {
                Log.e("clicked","${position}+?????? ?????????")
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

    // ????????? ?????? ??????
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit ??????
            .baseUrl(SearchActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoSearchAPI::class.java)   // ?????? ?????????????????? ????????? ??????
        val call = api.getSearchKeyword(SearchActivity.API_KEY, keyword)   // ?????? ?????? ??????

        // API ????????? ??????
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // ?????? ?????? (?????? ????????? response.body()??? ????????????)
                addRecyclerItems(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // ?????? ??????
                Log.e("SearchActivity", "?????? ??????: ${t.message}")
            }
        })
    }

    private fun addRecyclerItems(searchResult : ResultSearchKeyword?){
        if(!searchResult?.documents.isNullOrEmpty()){
            //???????????? ??????
            listItems.clear() //????????? ?????????
            for(document in searchResult!!.documents){
                //????????? ??????????????? ?????? ??????
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

    //?????? ??????
    fun applyFilter(){
        getRooms()
        filterLayout.visibility=View.GONE
    }

    //?????? ????????? ????????????
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


    //???????????? ??????
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
        //?????? ??????
        mapView?.removeAllPOIItems()
        for(i in 0..roomDTOList.size-1){
            val marker = MapPOIItem()
            marker?.apply {
                itemName = "${roomDTOList[i].deposit}/${roomDTOList[i].monthlyFee}" // ?????? ??????
                marker.tag = i
                mapPoint = MapPoint.mapPointWithGeoCoord(roomDTOList[i].address.latitude.toDouble()//?????? ??????
                        ,roomDTOList[i].address.longitude.toDouble())
                Log.e("lati",roomDTOList[i].address.latitude+"\t"+roomDTOList[i].address.longitude)
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
            marker.isShowDisclosureButtonOnCalloutBalloon
        }
        setRecyclerAdapter()
    }

    fun findAndSetClusters(){
         //?????? ?????? ?????? ????????? cluster??? ??????????????????
        clusterList.clear()
        clusterCount = roomDTOList.size/3
        Log.e("???","clusterCount : $clusterCount")


        if(clusterCount>0){
            var clusteringCount = 0

            //step 1 ????????? roomDTO??? center??? ??????
            for(i in 0 until clusterCount){
                clusterList.add(roomDTOList[i])
            }


            // 5??? ????????????.
            while(clusteringCount!=5){
                positionCluster.clear()
                //step 2 roomDTOList??? ???????????? ?????? ?????? ????????? cluster??? ?????????.
                for(i in 0 until roomDTOList.size){
                    var distance = 10000.01
                    var position = 0
                    for(j in 0 until clusterCount){
                        val d = calculateDistance(roomDTOList[i],clusterList[j])  //??????????????? ???????????? ????????? ??????
                        if(d<distance){
                            distance = d
                            position = j
                        }
                    }
                    positionCluster.add(position)
                }

                //step 3 ?????? ????????? cluster???????????? ?????? ??????
                for(i in 0 until clusterCount){
                    var centerX = 0.0
                    var centerY = 0.0
                    var count = 0
                    for(j in 0 until roomDTOList.size){
                        if(positionCluster[j]==i){
                            //?????? ??????????????? ?????? ?????? ?????????
                            centerX += roomDTOList[j].address.longitude.toDouble()
                            centerY += roomDTOList[j].address.latitude.toDouble()
                            count++
                        }
                    }
                    //???????????? ????????? ??????
                    clusterList[i].address.latitude = (centerY/count.toDouble()).toString()
                    clusterList[i].address.longitude = (centerX/count.toDouble()).toString()
                }

                clusteringCount++
            }

            setClusters(positionCluster)


        }else{ // ??? ????????? ???????????????

        }
    }

    //??? ?????? ????????? ????????? ????????? ??????
    fun calculateDistance(a : RoomDTO, b : RoomDTO) : Double{
        val x = a.address.latitude.toFloat() - b.address.latitude.toFloat()
        val y = a.address.longitude.toFloat() - b.address.longitude.toFloat()

        Log.e("???","???????????? ?????? : ${hypot(x,y)}")
        return hypot(x,y).toDouble() // ??? ?????? ????????? ?????? ???????????? ????????? ??????
    }

    fun setClusters(positionCluster : ArrayList<Int>){
        mapView?.removeAllPOIItems()
        val clusterItems : ArrayList<RoomDTO> = arrayListOf()
        for(i in 0 until clusterList.size){
            clusterItems.clear()

            //????????????????????? ?????? ?????????
            for(j in 0 until positionCluster.size){
                if(positionCluster[j] == i){
                    clusterItems.add(roomDTOList[j])
                }
            }

            var marker = MapPOIItem()
            marker.apply {
                itemName = "?????????" // ?????? ??????
                marker.tag = clusterTag
                mapPoint = MapPoint.mapPointWithGeoCoord(clusterList[i].address.latitude.toDouble()//?????? ??????
                    ,clusterList[i].address.longitude.toDouble())
                Log.e("????????????","${clusterList[i].address.latitude}   ,  ${clusterList[i].address.longitude}")
                markerType = MapPOIItem.MarkerType.CustomImage
                customImageResourceId = R.drawable.ic_cluster
                selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                customSelectedImageResourceId = R.drawable.ic_cluster_selected
                isCustomImageAutoscale = false
                userObject = clusterItems
                setCustomImageAnchor(0.5f, 1.0f)
            }
            mapView?.addPOIItem(marker)
            marker.isShowDisclosureButtonOnCalloutBalloon
        }
        setRecyclerAdapter()
    }

    fun setRecyclerAdapter(){
        recyclerView?.adapter = adapter
    }

    fun getRooms(){
        when(roomkinds){
            -1 ->{ //????????????
                getRoomDTOListAndSetMarkers(-1)
            }
            0 ->{ //??????
                getRoomDTOListAndSetMarkers(0)
            }
            1 ->{ //???, ?????????
                getRoomDTOListAndSetMarkers(1)
            }
            2 ->{ //????????????
                getRoomDTOListAndSetMarkers(2)
            }
            3 ->{ //???????????????
                getRoomDTOListAndSetMarkers(3)
            }
        }


    }
    fun getRoomDTOListAndSetMarkers(roomKinds : Int){
        db.collection("rooms").get().addOnSuccessListener { documents ->
            roomDTOList.clear()
            for(document in documents){
                val dto = document.toObject(RoomDTO::class.java)
                if(contractType==0){ //??????
                    if(roomKinds== -1){ //????????????
                        if(dto.deposit<=maxDepositFee&&dto.deposit>=minDepositFee
                            &&dto.adminFee>=minAdminFee&&dto.adminFee<=maxAdminFee
                            &&dto.monthlyFee<=maxMonthlyFee&&dto.monthlyFee>=minMonthlyFee
                            &&dto.contractType==contractType){
                            roomDTOList.add(dto)
                        }
                    }else{ //???????????? ?????? ??????
                        if(dto.deposit<=maxDepositFee&&dto.deposit>=minDepositFee
                            &&dto.adminFee>=minAdminFee&&dto.adminFee<=maxAdminFee
                        &&dto.monthlyFee<=maxMonthlyFee&&dto.monthlyFee>=minMonthlyFee
                        &&dto.contractType==contractType&&dto.roomKinds==roomKinds){
                            roomDTOList.add(dto)
                        }
                    }
                }else{ //??????
                    if(roomKinds== -1){ //????????????
                        if(dto.deposit<=maxDepositFee&&dto.deposit>=minDepositFee
                            &&dto.adminFee>=minAdminFee&&dto.adminFee<=maxAdminFee
                            &&dto.contractType==contractType){
                            roomDTOList.add(dto)
                        }
                    }else{ //???????????? ?????? ??????
                        if(dto.deposit<=maxDepositFee&&dto.deposit>=minDepositFee
                            &&dto.adminFee>=minAdminFee&&dto.adminFee<=maxAdminFee
                            &&dto.contractType==contractType&&dto.roomKinds==roomKinds){
                            roomDTOList.add(dto)
                        }
                    }
                }
            }

            filteringForFloorNum()


            setMarkers()
        }
    }

    fun filteringForFloorNum(){
        var j = 0
        for(i in 0 until roomDTOList.size){
            when(roomDTOList[j].floorNumber){
                0 ->{
                    if(floorNumArr[0]!=1){ //????????? ?????? ?????? ??????????????????
                        roomDTOList.removeAt(j)
                        j--
                    }
                }
                1 ->{
                    if(floorNumArr[1]!=1){ //1??? ?????? ?????? ??????????????????
                        roomDTOList.removeAt(j)
                        j--
                    }
                }
                2 ->{
                    if(floorNumArr[2]!=1){ //2??? ?????? ?????? ??????????????????
                        roomDTOList.removeAt(j)
                        j--
                    }
                }
                3 ->{
                    if(floorNumArr[3]!=1){ //3??? ?????? ?????? ??????????????????
                        roomDTOList.removeAt(j)
                        j--
                    }
                }
                4 ->{
                    if(floorNumArr[4]!=1){ //4??? ?????? ?????? ??????????????????
                        roomDTOList.removeAt(j)
                        j--
                    }
                }
                else ->{
                    if(floorNumArr[5]!=1){ //5??? ?????? ?????? ?????? ??????????????????
                        roomDTOList.removeAt(j)
                        j--
                    }
                }
            }
            j++
        }
    }

    //????????? ????????? ?????????
    class CustomBalloonAdapter : CalloutBalloonAdapter{
        override fun getCalloutBalloon(p0: MapPOIItem?) : View?{
            //?????? ????????? ????????? ?????????
            return null
        }
        override fun getPressedCalloutBalloon(p0: MapPOIItem?): View? {
            //????????? ?????? ???
            return null
        }
    }

    //mapViewEventListener
    override fun onMapViewInitialized(p0: MapView?) {}

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        Log.e("??????","????????? ${p1}")

        if(p1>=7&&!isClusterVisible){ //???????????? ???????????? ??????
            findAndSetClusters()
            isClusterVisible = true
            Log.e("???","?????????????????????")
        }

        if(p1<=6&&isClusterVisible){//???????????? ???????????????
            setMarkers()
            isClusterVisible = false
            Log.e("???","????????????????????????")
        }
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

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {}

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {}

    //?????? ??????????????? ?????????
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

        if (p1 != null) {
            if(p1.tag==myLocMarkerTag){ //????????? ??????

            }else if(p1.tag == clusterTag){//???????????? ??????
                val clusterItems = p1.userObject as ArrayList<RoomDTO>

                if(!isMarkerClicked) { //??????????????? recyclerview ?????????
                    isMarkerClicked = true
                    currentItems.clear()
                    currentItems.addAll(clusterItems)
                    mapView?.setMapCenterPoint(p1.mapPoint,true)
                    if(!isFirstClicked){ //???????????? ????????? ?????????
                        adapter = KakaoMapAdapter(currentItems)
                        setRecyclerAdapter()
                        isFirstClicked = true
                    }else{
                        adapter?.notifyDataSetChanged()
                    }
                    recyclerView?.visibility = View.VISIBLE

                }else if(isMarkerClicked){ //????????? ?????? ????????? recyclerview ??????
                    isMarkerClicked = false
                    recyclerView?.visibility = View.GONE
                }

            } else{ //??? ??????
                roomDTOList[p1.tag]


                if(!isMarkerClicked) { //??????????????? recyclerview ?????????
                    isMarkerClicked = true
                    roomDTOs.clear()
                    roomDTOs.add(roomDTOList[p1.tag])
                    currentItems.clear()
                    currentItems.addAll(roomDTOs)
                    mapView?.setMapCenterPoint(p1.mapPoint,true)
                    if(!isFirstClicked){ //???????????? ????????? ?????????
                        adapter = KakaoMapAdapter(currentItems)
                        setRecyclerAdapter()
                        isFirstClicked = true
                    }else{
                        adapter?.notifyDataSetChanged()
                    }
                    recyclerView?.visibility = View.VISIBLE

                }else if(isMarkerClicked){ //????????? ?????? ????????? recyclerview ??????
                    isMarkerClicked = false
                    recyclerView?.visibility = View.GONE
                }
            }
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {}

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {}

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
}