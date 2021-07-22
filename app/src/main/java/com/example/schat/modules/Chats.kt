package com.example.schat.modules

import java.util.*

data class Chats(
    val msg: String,
    var from: String,
    var name: String,
    var image: String,
    val time: Date = Date(),
    var count: Int = 0
) {
    constructor() : this("", "", "", "", Date(), 0)
}