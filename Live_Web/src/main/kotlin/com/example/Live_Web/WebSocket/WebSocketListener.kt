package com.example.Live_Web.WebSocket

import com.example.Live_Web.DatabaseRepositories.FileRepository
import com.example.Live_Web.DatabaseRepositories.UserRepository
import com.example.Live_Web.Helpers.RandomStrings
import com.example.Live_Web.Models.File
import com.example.Live_Web.Models.User
import com.example.Live_Web.Models.Websocket.Connect
import com.example.Live_Web.Models.Websocket.DataElement
import com.example.Live_Web.Models.Websocket.Message
import com.example.Live_Web.Models.Websocket.Modify
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.typesafe.config.ConfigException.Null
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import netscape.javascript.JSObject
import java.util.*
import java.util.concurrent.atomic.*
import kotlin.collections.LinkedHashSet

class WebSocketListener (
    val gson: Gson,
    val randomStrings: RandomStrings
) {
    fun embeddedServerInit() {
        embeddedServer(Netty, port = 9001) {
            install(WebSockets)
            routing {
                val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

                // SEND A JSON OF {fileId and userId}
                webSocket("/connect") {
                    //send("You have called 'CONNECT'!")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        /**
                         * CALL FUNC HERE
                         */
                        println(receivedText)

                        val connect: Connect  = gson.fromJson(receivedText, Connect::class.java)

//                        val file = fileRepository.findOneById(connect.fileId)
//                        val user = userRepository.findOneById(connect.userId)
//                        if (file.sharedIds.contains(connect.userId)) {
                        val thisConnection = Connection(this, connect.fileId, connect.userId, connect.ownerId)
                        connections+= thisConnection
//                        }

                    }

                }

                webSocket("/close") {
                    //send("You have called 'CLOSE'!")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        /**
                         * CALL FUNC HERE
                         */
                        println(receivedText)
                        val connect: Connect  = gson.fromJson(receivedText, Connect::class.java)

//                        val file = fileRepository.findOneById(connect.fileId)
//                        val user = userRepository.findOneById(connect.userId)
//                        if (file.sharedIds.contains(connect.userId)) {

                            for (i in connections) {
                                if (i.fileId == connect.fileId && i.userId == connect.userId) {
                                    connections.remove(i)
                                }
                            }
//                        }

                    }

                }

                webSocket("/modify") {
                    // send("You have called 'CONNECT'!")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        println(receivedText)
                        /**
                         * CALL FUNC HERE
                         */
                        val modify: Modify  = gson.fromJson(receivedText, Modify::class.java)

                        val path = "../../USER_FILES/${modify.ownerId}/${modify.fileId}.json"
                        val jsonText = java.io.File(path).readText(Charsets.UTF_8)
                        val typeToken = object : TypeToken<List<DataElement>>() {}.type


                        val fileData  = gson.fromJson<List<DataElement>>(jsonText, typeToken)
                        var jsonData = mutableListOf<DataElement>()
                        if (fileData != null) {
                            jsonData = fileData.toMutableList()
                        }

                        var id = modify.dataId
                        when (modify.action){
                            0 -> {
                                id = randomStrings.getRandomString(16)
                                jsonData.add(DataElement(id, modify.data))
                            }
                            1 -> {
                                for (i in jsonData) {
                                    if (i.id == modify.dataId) {
                                        i.data = modify.data
                                        break
                                    }
                                }
                            }
                            2 -> {
                                for (i in jsonData) {
                                    if (i.id == modify.dataId) {
                                        jsonData.remove(i)
                                        break
                                    }
                                }
                            }
                        }

                        val newJsonText = gson.toJson(jsonData)
                        java.io.File(path).writeText(newJsonText)

                        for (i in connections) {
                            if (i.fileId == modify.fileId /*&& i.userId != modify.userId*/) {
                                val sendText = Modify(modify.fileId, i.userId, modify.ownerId, modify.action, id, modify.data)
                                i.session.send(gson.toJson(sendText))
                            }
                        }

                    }

                }

                webSocket("/message") {
                    // send("You have called 'CONNECT'!")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        println(receivedText)
                        /**
                         * CALL FUNC HERE
                         */
                        val message: Message  = gson.fromJson(receivedText, Message::class.java)


                        for (i in connections) {
                            if (i.fileId == message.fileId && i.userId != message.userId) {
                                val sendText = Message(message.fileId, message.userId, message.text)
                                i.session.send(gson.toJson(sendText))
                            }
                        }

                    }

                }
            }
        }.start(wait = true)
    }

}


class Connection constructor(val session: DefaultWebSocketSession, val fileId: String, val userId: String, val ownerId: String) {

    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"

}
