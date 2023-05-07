package com.gui

import com.control.Model
import com.types.IView
import com.types.SideContent
import com.types.User
import com.types.ViewString
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text

internal class MenuBar(private val model: Model): IView {
    private var view: HBox? = null
    private val authBox = HBox(10.0)
    private val signInBtn = Button("Sign In")
    private val signUpBtn = Button("Sign Up")
    private val signOutBtn = Button("Sign Out")
    private val settingsBtn = Button("Change Password")
    private val hotkeyBtn = Button("Hotkeys")
    private val welcomeText = Text("")
//    private val chatBtn = Button("Chat")

    override fun updateView() {
        authBox.children.clear()
        if (model.currentUser == null) {
            authBox.children.add(signInBtn)
            authBox.children.add(signUpBtn)
            welcomeText.text = ""
        } else {
            authBox.children.add(settingsBtn)
            authBox.children.add(signOutBtn)
            welcomeText.text = "Welcome back ${model.currentUser?.username}"
        }
    }


    init {
        val box = HBox(10.0)
        val region = Region()
        HBox.setHgrow(region, Priority.ALWAYS)
        welcomeText.font =  Font.font("Verdana", FontWeight.BOLD, 12.0)
        box.alignment = Pos.CENTER_LEFT
        box.background = Background(BackgroundFill(Color.LIGHTGRAY, null, null))
        box.padding = Insets(15.0, 12.0, 15.0, 12.0)
        box.children.add(region)
        box.children.add(welcomeText)
        box.children.add(authBox)

        val switchThemeBtn = Button("Switch Theme")
        box.children.add(hotkeyBtn)
        box.children.add(switchThemeBtn)
//        box.children.add(chatBtn)

//        chatBtn.setOnMouseClicked {
//            model.openChat()
//        }

        signInBtn.setOnMouseClicked {
            model.updateSideContent(SideContent.SIGNIN_FORM)
        }

        signUpBtn.setOnMouseClicked {
            model.updateSideContent(SideContent.SIGNUP_FORM)
        }

        signOutBtn.setOnMouseClicked {
            model.signOutUser()
        }

        settingsBtn.setOnMouseClicked {
            model.updateSideContent(SideContent.ACCOUNT_SETTINGS)
        }

        hotkeyBtn.setOnMouseClicked {
            model.updateSideContent(SideContent.HOTKEYS)
        }
        switchThemeBtn.setOnMouseClicked {
            model.switchTheme()
        }

        view = box
        model.addView(this, ViewString.MENUBAR)
    }

    fun getView(): HBox? {
        return view
    }
}