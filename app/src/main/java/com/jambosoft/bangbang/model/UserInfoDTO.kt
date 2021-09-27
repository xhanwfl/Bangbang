package com.jambosoft.bangbang.model

data class UserInfoDTO(
    var email : String = "",
    var profileUrl : String = "",
    var name : String = "이름을 변경해주세요",
    var hp : String ="",
    var hpAuth : Boolean = false,
    var token : String = "",
    var alramCount : Int = 0,
    var tokenType : String = "n"
)
