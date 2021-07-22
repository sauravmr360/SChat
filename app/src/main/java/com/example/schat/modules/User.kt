package com.example.schat.modules

import com.google.firebase.firestore.FieldValue

data class User(
    val name:String,
    val imageurl:String,
    val thumbimage:String,
    val uid:String,
    val devicetoken:String,
    val status:String,
    val onlinestatus: String
) {

    //for firebase you have to make an empty constructor always
    constructor():this("","","","","","","")

    constructor(name: String,imageurl: String,thumbimage: String,uid: String):this(
        name,
        imageurl,
        thumbimage,
        uid,
        "",
        "Hey! I am using S-Chat",
        " "
    )
}