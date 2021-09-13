package com.jambosoft.bangbang.model

data class UserInfoDTO(
    var email : String = "",
    var profileUrl : String = "",
    var name : String = "",
    var hp : String ="",
    var hpAuth : Boolean = false,
    var token : String = "",
    var alramCount : Int = 0
)
