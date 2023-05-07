package com.gui

import com.control.Model
import com.types.IView
import com.types.Theme
import com.types.ViewString
import com.types.darkThemeBackgroundColor
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.Stage

internal class Sidebar(private val model: Model): IView {
    private var view: VBox? = null
    private val fileText = Text("Files")

    private fun updateTheme() {
        val cssLayout = """
            -fx-border-color: transparent ${if (model.currentTheme == Theme.LIGHT) "black" else "white"} transparent transparent;
            -fx-border-width: 1;
            -fx-border-style: solid;
            
            """.trimIndent()
        view?.style = cssLayout
        val color = if (model.currentTheme == Theme.LIGHT) Color.WHITE else darkThemeBackgroundColor
        view?.background = Background(BackgroundFill(color, null, null))
        fileText.fill = if (model.currentTheme == Theme.LIGHT) Color.BLACK else Color.WHITE
    }
    override fun updateView() {
        updateTheme()
    }

    init {
        val box = VBox(5.0)
        box.padding = Insets(15.0, 12.0, 15.0, 12.0)
        fileText.font =  Font.font("Verdana", FontWeight.BOLD, 15.0)
        val openFromComputerBtn = Button("My Computer")
        val openFromCloudBtn = Button("My Cloud")

        openFromComputerBtn.setOnMouseClicked {
            model.openFileMenu()
        }

        openFromCloudBtn.setOnMouseClicked {
            model.openCloudFiles()
        }

        box.children.add(fileText)
        box.children.add(openFromComputerBtn)
        box.children.add(openFromCloudBtn)

        view = box
        model.addView(this, ViewString.SIDEBAR)
    }

    fun getView(): VBox? {
        return view
    }
}