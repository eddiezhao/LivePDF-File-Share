package com.control
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent

import javafx.event.EventHandler
import javafx.scene.layout.VBox
import javafx.stage.Stage

class Controller(model:Model) {
    private val curmodel = model
    fun openLastFile() {
        curmodel.openLastFile()
    }
    fun openFileMenu() {
        curmodel.openFileMenu()
    }
    fun bookmarkLastFile() {
        curmodel.bookmarkLastFile()
    }
}

class ShortcutHandler(model:Model) {
    private var model = model
    var shortcutMap : HashMap<KeyCode, String> = HashMap<KeyCode, String> ()
    private var c = Controller(model)
    fun init() {
        shortcutMap[KeyCode.L] = "Open Last file"
        shortcutMap[KeyCode.O] = "Open File Menu"
        shortcutMap[KeyCode.B] = "Bookmark Last File"
    }
    fun control(event: KeyEvent) {
        if (event.code in shortcutMap) {
            when (shortcutMap[event.code]) {
                "Open Last file" -> c.openLastFile()
                "Open File Menu" -> c.openFileMenu()
                "Bookmark Last File" -> c.bookmarkLastFile()
                else -> {}
            }
        }
    }
    private fun getNewKey(key: KeyCode) : KeyCode {
        var newKey = key
        var stage = Stage()
        val screen = VBox()
        val scene = Scene(screen, 200.0, 100.0)
        scene.onKeyPressed = EventHandler(
            fun(event: KeyEvent) {
                newKey = event.code
                stage.close()
            }
        )
        stage.title = "Press on any key"
        stage.scene = scene
        stage.showAndWait()

        return newKey
    }
    fun modify(key: KeyCode) : KeyCode {
        var newKey = getNewKey(key)
        if (shortcutMap.contains(newKey)) {
            return key
        }
        if (newKey !== key && (newKey.isDigitKey || newKey.isLetterKey)) {
            val action = shortcutMap[key]
            shortcutMap.remove(key)
            if (action != null) {
                shortcutMap[newKey] = action
            }
        }
        return newKey
    }
}