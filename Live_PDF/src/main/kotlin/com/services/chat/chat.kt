package com.services

import com.WebsocketClient.WebsocketSetup
import javafx.animation.AnimationTimer
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.util.*


class Chat(username: String, wsClient: WebsocketSetup) {
    private var messages: MutableList<Label> = ArrayList<Label>()
    private var index = 0
    private val chatBox = VBox(4.0)
    val container = ScrollPane()
    private val username = username
    private val wsClient = wsClient
    private var receivedMessages: MutableList<Pair<String, String>> = ArrayList<Pair<String, String>>()

    fun chatInit(stage: Stage) {

        val chatLayout = GridPane()
        val sceneTwo = Scene(chatLayout, 500.0, 500.0)
        val chatBoxWindow = Stage()

        chatBoxWindow.title = "Live Chat"
        chatBoxWindow.scene = sceneTwo

        chatBoxWindow.x = stage.getX() + 482
        chatBoxWindow.y = stage.getY() + 100

        val send = Button("Send")
        val message = TextField()

        container.setPrefSize(400.0, 400.0)
        container.content = chatBox
        chatBox.padding = Insets(10.0, 10.0, 10.0, 10.0)

        message.promptText = "Type your message"
        message.prefColumnCount = 10
        message.padding = Insets(10.0, 10.0, 10.0, 10.0)

        send.padding = Insets(10.0, 10.0, 10.0, 10.0)
        GridPane.setConstraints(container, 0, 0)
        GridPane.setConstraints(send, 1, 1)
        GridPane.setConstraints(message, 0, 1)

        send.setOnMouseClicked {
            sendMessage(message)
        }

        message.setOnKeyPressed {
            if (it.code.equals(KeyCode.ENTER)) {
                sendMessage(message)
            }
        }

        chatLayout.children.addAll(container, send, message)
        chatLayout.padding = Insets(10.0, 10.0, 10.0, 10.0)

        val timer: AnimationTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                checkForMessages()
            }
        }
        timer.start()

        chatBoxWindow.show()
    }

    fun sendMessage(message: TextField) {
        if ((message.text != null && !message.text.isEmpty())) {
            wsClient.sendMessage(message.text)
            var printUsername = Label(username)
            printUsername.textFill = Color.web("#0076a3")
            messages.add(printUsername)
            messages.add(Label(message.text))
            chatBox.children.add(printUsername)
            chatBox.children.add(Label(message.text))
            message.clear()
            index += 2
        } else {

        }
    }

    fun checkForMessages() {
        for (i in receivedMessages) {
            try {
                var printUsername = Label(i.first)
                printUsername.textFill = Color.web("#bf4559")
                messages.add(printUsername)
                messages.add(Label(i.second))
                chatBox.children.add(printUsername)
                chatBox.children.add(Label(i.second))
            } catch (e: Exception) {
                println(e)
            }

        }
        receivedMessages.clear()

    }

    fun displayMessage(usernamez: String, message: String) {
        if (message != null) {
            receivedMessages.add(Pair(usernamez, message))
//            var printUsername = Label(usernamez)
//            printUsername.textFill = Color.web("#bf4559")
//            messages.add(printUsername)
//            messages.add(Label(message))
//            chatBox.children.add(printUsername)
//            chatBox.children.add(Label(message))
//            index += 2
        } else {

        }
    }

}