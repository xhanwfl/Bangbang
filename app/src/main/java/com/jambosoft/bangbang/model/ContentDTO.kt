package com.jambosoft.bangbang.model

data class ContentDTO(
    var title : String ="",
    var content : String ="",
    var userId : String ="",
    var timestamp : Long? = null,
    var favoriteCount : Int = 0,
    var favorites : MutableMap<String,Boolean> = HashMap()){

    data class Comment(var uid: String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp : Long? = null)
}
