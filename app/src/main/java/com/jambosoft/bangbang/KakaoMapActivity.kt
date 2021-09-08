package com.jambosoft.bangbang

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jambosoft.bangbang.Adapter.DetailRoomImageAdapter
import com.jambosoft.bangbang.Adapter.KakaoMapAdapter
import com.jambosoft.bangbang.model.RoomDTO
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapView

import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint


class KakaoMapActivity : AppCompatActivity(), MapView.MapViewEventListener, MapView.POIItemEventListener {
    var latitude : Double = 0.0
    var longitude : Double = 0.0
    var depositFee : Int = 0
    var monthlyFee : Int = 0
    var roomkinds : Boolean = false
    var roomDTOList : ArrayList<RoomDTO>? = null
    var roomDTOs : ArrayList<RoomDTO>? = null
    var mapView : MapView? = null
    var mapViewContainer : ViewGroup? = null
    var recyclerView : RecyclerView? = null
    var isMarkerClicked = false
    var isFirstClicked = false
    var adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

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
        depositFee = intent.getIntExtra("depositFee",0)
        monthlyFee = intent.getIntExtra("monthlyFee",0)
        roomkinds = intent.getBooleanExtra("roomkinds",false)

        recyclerView = findViewById(R.id.kakaomap_content_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        Log.e("wow","latitude : ${latitude}\nlongitude : ${longitude}\ndepositFee : ${depositFee}\nmonthlyFee : ${monthlyFee}\nroomkinds : ${roomkinds}\n")

        //마커 이벤트 리스너
       // val myPOIItemEventListener = MyPOIItemEventListener()

        //맵뷰 이벤트 리스너
       // val myMapViewEventListener = MyMapViewEventListener()
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







        //방 정보 가져와서 roomDTOList에 추가
        getRooms()

        mapViewContainer?.addView(mapView)

    }

    fun setMarkers(){
        //마커 추가
        if(roomDTOList!!.size>0){
            for(i in 0..roomDTOList!!.size-1){
                var marker = MapPOIItem()
                marker?.apply {
                    itemName = "${roomDTOList!![i].deposit}/${roomDTOList!![i].monthlyFee}" // 마커 이름
                    marker.tag = i
                    mapPoint = MapPoint.mapPointWithGeoCoord(roomDTOList!![i].address.latitude.toDouble()//마커 좌표
                        ,roomDTOList!![i].address.longitude.toDouble())
                    Log.e("lati",roomDTOList!![i].address.latitude+"\t"+roomDTOList!![i].address.longitude)
                    markerType = MapPOIItem.MarkerType.BluePin
                    //customImageResourceId = R.drawable.ic_marker
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                    //customSelectedImageResourceId = R.drawable.ic_marker_clicked
                    //isCustomImageAutoscale = false
                    //setCustomImageAnchor(0.5f, 1.0f)
                }
                mapView?.addPOIItem(marker)
            }

            setRecyclerAdapter()
        }
    }

    fun setRecyclerAdapter(){
        recyclerView?.adapter = adapter
    }

    fun getRooms(){
        val db =FirebaseFirestore.getInstance()
        db.collection("rooms")
            .whereLessThanOrEqualTo("monthlyFee",monthlyFee)
            .get().addOnSuccessListener { documents ->
                roomDTOList?.clear()
                for(document in documents){
                   var dto = document.toObject(RoomDTO::class.java)
                    if(dto.deposit<=depositFee&&dto.roomKinds==roomkinds){
                        roomDTOList?.add(dto)
                        Log.e("dto","${dto.deposit}/${dto.monthlyFee}")
                    }
                }
                setMarkers()
            }.addOnFailureListener {
                Log.e("실패",it.printStackTrace().toString())
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
        isMarkerClicked = false
        recyclerView?.visibility = View.GONE
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

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?, p2: MapPOIItem.CalloutBalloonButtonType?) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

}