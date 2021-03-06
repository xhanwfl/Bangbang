package com.jambosoft.bangbang.model

import java.io.Serializable


data class RoomDTO( //전부다 초기화 해야 쿼리로 객체를 가져올때 오류가 안남
    var images : ArrayList<String> = arrayListOf(),
    var address : RoomLocationInfoDTO = RoomLocationInfoDTO(),
    var deposit : Int = 0,
    var monthlyFee : Int = 0,
    var adminFee : Int = 0,
    var floorNumber : Int = 1,
    var roomKinds : Int = 0,
    var contractType : Int = 0,
    var info : RoomInfoDTO = RoomInfoDTO(),
    var moreInfo : RoomMoreInfoDTO = RoomMoreInfoDTO(),
    var userId : String = "",
    var uid : String = "",
    var timestamp : Long = 0,
    var imageCount : Int = 0,
    var favoriteCount : Int = 0,
    var favorites : MutableMap<String,Boolean> = HashMap(),
    var recents : MutableMap<String,Long> = HashMap(),
    var inquireCount : Int = 0,
    var hp : String = ""
) : Serializable {
    data class Comment(var uid : String = "",
                       var userId : String = "",
                       var comment : String = "",
                       var timestamp : Long = 0) : Serializable
}

data class RoomLocationInfoDTO(
    var name : String="",
    var road : String="",
    var address : String="",
    var latitude : String="",
    var longitude : String=""
) : Serializable

data class RoomInfoDTO(
    var title : String="",
    var explain : String=""
) : Serializable

data class RoomMoreInfoDTO(
    var area : Int = 0,
    var options : String="",
    var parking : String="",
    var term : String="",
    var movein : String=""
) : Serializable
