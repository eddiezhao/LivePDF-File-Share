package com.Model.Websocket

data class Message(
    val fileId: String,
    val userId : String,
    val text: String
)
