package com.Model.Websocket

data class Modify(
    val fileId: String,
    val userId : String,
    val ownerId : String,
    val action: Int, // 0 is ADD, 1 is UPDATE, 2 is DELETE
    val dataId: String, // LEAVE IT EMPTY IF YOU DELETE
    val data: String
)

