package com.example.Live_Web.Models.Websocket

data class Message(
    val fileId: String,
    val userId : String,
    val text: String
)
