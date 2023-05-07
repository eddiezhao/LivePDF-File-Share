package com.services

import com.types.ToastMessage
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration


class Toast(stage: Stage) {
    private val toastDelay = 3000
    private val fadeInDelay = 500
    private val fadeOutDelay = 500
    private var toastStage: Stage = Stage()

    init {
        toastStage.initOwner(stage)
        toastStage.isResizable = false
        toastStage.initStyle(StageStyle.TRANSPARENT)
    }

    fun makeText(toastMsg: String?, type: ToastMessage) {
        val text = Text(toastMsg)
        text.font = Font.font("Verdana", FontWeight.BOLD, 15.0)

        when(type) {
            ToastMessage.ERROR -> text.fill = Color.RED
            ToastMessage.SUCCESS -> text.fill = Color.GREEN
        }

        val root = StackPane(text)
        root.style = "-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 50px;"
        root.opacity = 0.0
        val scene = Scene(root)
        scene.fill = Color.TRANSPARENT
        toastStage.scene = scene
        toastStage.show()
        val fadeInTimeline = Timeline()
        val fadeInKey1 =
            KeyFrame(Duration.millis(fadeInDelay.toDouble()), KeyValue(toastStage.scene.root.opacityProperty(), 1))
        fadeInTimeline.keyFrames.add(fadeInKey1)
        fadeInTimeline.onFinished = EventHandler { _ ->
            Thread {
                try {
                    Thread.sleep(toastDelay.toLong())
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                val fadeOutTimeline = Timeline()
                val fadeOutKey1 = KeyFrame(
                    Duration.millis(fadeOutDelay.toDouble()),
                    KeyValue(toastStage.scene.root.opacityProperty(), 0)
                )
                fadeOutTimeline.keyFrames.add(fadeOutKey1)
                fadeOutTimeline.onFinished =
                    EventHandler { _ -> toastStage.close() }
                fadeOutTimeline.play()
            }.start()
        }
        fadeInTimeline.play()
    }
}