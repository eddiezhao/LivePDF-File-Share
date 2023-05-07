package com.example.Live_Web

import com.example.Live_Web.DatabaseRepositories.FileRepository
import com.example.Live_Web.DatabaseRepositories.UserRepository
import com.example.Live_Web.Helpers.RandomStrings
import com.example.Live_Web.WebSocket.WebSocketListener
import com.google.gson.Gson
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

@SpringBootApplication
class LiveWebApplication


/**
 * Main Method to run
 * Generates Live Web Application on:
 * http://localhost:9000
 */
fun main(args: Array<String>) {
	runApplication<LiveWebApplication>(*args)

	val wsl = WebSocketListener(Gson(), RandomStrings())
	wsl.embeddedServerInit()
}

