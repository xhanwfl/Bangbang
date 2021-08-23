package com.jambosoft.bangbang.model

data class SearchContentDTO(val name: String, // 장소명
                       val road: String,      // 도로명 주소
                       val address: String,   // 지번 주소
                       val x: String,         // 경도(Longitude)
                       val y: String)         // 위도(Latitude)
