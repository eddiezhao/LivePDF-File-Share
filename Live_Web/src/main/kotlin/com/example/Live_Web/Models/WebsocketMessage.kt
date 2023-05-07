package com.example.Live_Web.Models

data class WebsocketMessage (
    val id: String,
    val fileId: String,
    val type: Action,
    val data: String
    )

enum class Action {
    ADD, DELETE, UPDATE
}