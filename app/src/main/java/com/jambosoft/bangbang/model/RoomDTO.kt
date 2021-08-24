package com.jambosoft.bangbang.model


data class RoomDTO(
    var images : ArrayList<String>,
    var address : RoomLocationInfoDTO,
    var deposit : String = "",
    var monthlyFee : String = "",
    var adminFee : String = "",
    var floorNumber : String = "",
    var roomKinds : String = "",
    var info : RoomInfoDTO,
    var moreInfo : RoomMoreInfoDTO,
    var userId : String? = null,
    var timestamp : Long? = null
)
data class RoomLocationInfoDTO(
    var name : String="",
    var road : String="",
    var address : String="",
    var latitude : String="",
    var longitude : String=""
)

data class RoomInfoDTO(
    var title : String="",
    var explain : String=""
)

data class RoomMoreInfoDTO(
    var kinds : String="",
    var area : String="",
    var options : String="",
    var parking : String="",
    var term : String="",
    var movein : String=""
)