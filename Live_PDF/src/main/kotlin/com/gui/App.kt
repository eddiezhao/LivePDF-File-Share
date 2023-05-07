package com.gui

import com.control.Model
import com.control.ShortcutHandler
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.stage.Stage


class App : Application() {
    override fun start(stage: Stage) {
        val model = Model(stage)
        val home = Home(model)
        val menubar = MenuBar(model)
        val sidebar = Sidebar(model)

        val screen = VBox()
        val main = HBox()
        main.children.add(sidebar.getView())
        main.children.add(home.getView())
        screen.children.add(menubar.getView())
        screen.children.add(main)

        stage.heightProperty().addListener { _, _, _ ->
            main.minHeight = stage.height
        }

        val scene = Scene(screen, 1000.0, 500.0)


        scene.onKeyPressed = EventHandler (
            fun(event : KeyEvent) {
                if (event.isShortcutDown) {
                    model.shortcut.control(event)
                }
            }
        )

        stage.title = "LivePDF"
        stage.scene = scene
        stage.show()
    }
}

