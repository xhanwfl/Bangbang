package com.jambosoft.bangbang.model

import java.io.Serializable

data class InquireDTO(
    var uid : String = "",
    var userId : String = "",
    var hp : String = "",
    var message : String = "",
    var roomId : String = "",
    var roomUserUid : String = "",
    var timestamp : Long = 0,
    var checked : Boolean = false
) : Serializable