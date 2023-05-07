package com.WebsocketClient

import com.Model.Websocket.Connect
import com.Model.Websocket.DataElement
import com.Model.Websocket.Message
import com.Model.Websocket.Modify
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import com.services.Chat
import com.services.APIClient

class ConnectListener (chat: Chat) : WebSocketListener() {
    val chatClient = chat
    override fun onOpen(webSocket: WebSocket, response: Response) {
        output("CONNECTED")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val annotationsFilePath = "./src/main/kotlin/assets/annotations.json"

            output("Received MOD: $text")

            val gson = Gson()

            val modify = gson.fromJson<Modify>(text, Modify::class.java)

            val jsonText = java.io.File(annotationsFilePath).readText(Charsets.UTF_8)
            val typeToken = object : TypeToken<List<DataElement>>() {}.type


            val fileData = gson.fromJson<List<DataElement>>(jsonText, typeToken)
            var jsonData = mutableListOf<DataElement>()
            if (fileData != null) {
                jsonData = fileData.toMutableList()
            }

            when (modify.action) {
                0 -> {
                    jsonData.add(DataElement(modify.dataId, modify.data))
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
            java.io.File(annotationsFilePath).writeText(newJsonText)
        } catch (e: Exception) {
            try {
                output("Received TEXT: $text")
                val gson = Gson()

                val message = gson.fromJson<Message>(text, Message::class.java)

                // ADD CODE HERE
                // print message on window
                // Using the API client call getUserById (username)
                // FORMAT is message. in username : message.text
                val api = APIClient()
                val user = api.getUserByUserId(message.userId)
                output("tmp")
                chatClient.displayMessage(user.username, message.text)

            } catch (e: Exception) {}
        }


    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
    }

    fun output(text: String?) {
        println(text)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}

class ModifyListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        output("CONNECTED")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        output("Received MODIFY : $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
    }

    fun output(text: String?) {
        println(text)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}

class ChatListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        output("CONNECTED")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        output("Received MESSAGE : $text")

    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }


    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
    }

    fun output(text: String?) {
        println(text)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}


class WebsocketSetup {
    private val gson = Gson()

    private var connestWs: WebSocket? = null
    private var modifyWs: WebSocket? = null
    private var chatWs: WebSocket? = null
    private var closeWs: WebSocket? = null

    private var fileId = ""
    private  var userId = ""
    private  var ownerId = ""

    private var chatClient: Chat? = null

    fun used() : Boolean {
        return fileId != ""
    }
    fun setup (fileId: String, userId: String, ownerId: String, chat: Chat) {
        this.fileId  = fileId
        this.userId = userId
        this.ownerId = ownerId
        chatClient= chat
        setupConnect(chat)
        connestWs?.send(gson.toJson(Connect(fileId,userId, ownerId)))
        setupModify()
        setupChat()
    }

    fun sendModification(action: Int, dataId: String, data:String) {
        val annotationsFilePath = "./src/main/kotlin/assets/annotations.json"

        val modify = Modify(fileId, userId, ownerId, action, dataId, data)
        modifyWs?.send(gson.toJson(modify))

        val jsonText = java.io.File(annotationsFilePath).readText(Charsets.UTF_8)
        val typeToken = object : TypeToken<List<DataElement>>() {}.type


        val fileData = gson.fromJson<List<DataElement>>(jsonText, typeToken)
        var jsonData = mutableListOf<DataElement>()
        if (fileData != null) {
            jsonData = fileData.toMutableList()
        }

        when (modify.action) {
            0 -> {
                jsonData.add(DataElement(modify.dataId, modify.data))
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
        java.io.File(annotationsFilePath).writeText(newJsonText)
    }

    fun sendMessage(data:String) {
        val message = Message(fileId, userId, data)
        chatWs?.send(gson.toJson(message))
    }
    fun close() {
        setupClose(chatClient!!)

        closeWs?.send(gson.toJson(Connect(fileId,userId, ownerId)))
        connestWs?.close(1000, null)
        modifyWs?.close(1000, null)
    }


    private fun setupConnect(chat: Chat) {

        val client = OkHttpClient()

        val request: Request = Request
            .Builder()
            .url("ws://localhost:9001/connect")
            .build()

        val listener = ConnectListener(chat)
        connestWs = client.newWebSocket(request, listener)

    }

    private fun setupModify() {
        val client = OkHttpClient()

        val request: Request = Request
            .Builder()
            .url("ws://localhost:9001/modify")
            .build()

        val listener = ModifyListener()
        modifyWs = client.newWebSocket(request, listener)
    }

    private fun setupChat(){
        val client = OkHttpClient()

        val request: Request = Request
            .Builder()
            .url("ws://localhost:9001/message")
            .build()

        val listener = ChatListener()
        chatWs = client.newWebSocket(request, listener)
    }

    private fun setupClose(chat: Chat) {

        val client = OkHttpClient()

        val request: Request = Request
            .Builder()
            .url("ws://localhost:9001/close")
            .build()

        val listener = ConnectListener(chat)
        closeWs = client.newWebSocket(request, listener)

    }

}
