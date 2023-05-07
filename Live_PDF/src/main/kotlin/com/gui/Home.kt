package com.gui

import com.control.Model
import com.types.*
import javafx.geometry.Insets
import javafx.scene.text.Text
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import java.io.File
import javafx.event.EventHandler

internal class Home(private val model: Model): IView {

    private var view: HBox? = null
    private val recentFileDisplay = VBox(4.0)
    private val bookmarkedFilesDisplay = VBox(4.0)
    private val heading = Text("LivePDF")
    private val bookmarkedText = Text("Bookmarked Files: ")
    private val recentText = Text("Recently Opened: ")
    private var textColor = Color.BLACK
    private var detailedCloudFile: CloudFile? = null

    private val rightBox = VBox(4.0)
    private val rightHeading = Text("")
    private fun displayFiles(files: ArrayList<String>, fileDisplay: VBox) {
        fileDisplay.children.clear()
        if (files.size > 0) {
            for (file in files) {
                val btn = Button(File(file).name)
                btn.setOnMouseClicked {
                    model.openFile(File(file))
                }
                fileDisplay.children.add(btn)
            }
        } else {
            val text = Text("No files in this category")
            text.fill = textColor
            fileDisplay.children.add(text)
        }
    }

    private fun displayCloudFiles() {
        rightHeading.text = "Your files:"
        rightBox.children.add(rightHeading)

        val ownedFilesText = Text("Owned by you:")
        ownedFilesText.fill = textColor
        rightBox.children.add(ownedFilesText)

        for (file in model.cloudFiles) {
            val btn = Button(file.name)
            btn.setOnMouseClicked {
                detailedCloudFile = file
                model.updateSideContent(SideContent.CLOUD_FILE_DETAIL)
            }
            rightBox.children.add(btn)
        }

        val sharedFilesText = Text("Shared with you:")
        sharedFilesText.fill = textColor
        rightBox.children.add(sharedFilesText)

        for (file in model.sharedFiles) {
            val btn = Button(file.name)
            btn.setOnMouseClicked {
                model.openFileFromCloud(file)
            }
            rightBox.children.add(btn)
        }

    }

    private fun updateTheme() {
        var backgroundColor = Color.WHITE
        textColor = Color.BLACK
        if (model.currentTheme == Theme.DARK) {
            backgroundColor = darkThemeBackgroundColor
            textColor = Color.WHITE
        }
        view?.background = Background(BackgroundFill(backgroundColor, null, null))
        heading.fill = textColor
        bookmarkedText.fill = textColor
        recentText.fill = textColor
        rightHeading.fill = textColor
    }

    private fun generateView() : HBox {
        val content = HBox(25.0)
        HBox.setHgrow(content, Priority.ALWAYS)
        val box = VBox(8.0)
        box.padding = Insets(15.0, 12.0, 15.0, 30.0)
        heading.font = Font.font("Verdana", FontWeight.BOLD, 18.0)
        val bookmarkBtn = Button("Bookmark last opened file")
        val uploadBtn = Button("Upload last opened file")
        val headingBox = HBox(10.0)
        headingBox.children.add(heading)
        headingBox.children.add(bookmarkBtn)
        headingBox.children.add(uploadBtn)
        box.children.add(headingBox)
        bookmarkedText.font = Font.font("Verdana", FontWeight.BOLD, 14.0)
        box.children.add(bookmarkedText)
        box.children.add(bookmarkedFilesDisplay)
        recentText.font = Font.font("Verdana", FontWeight.BOLD, 14.0)
        box.children.add(recentText)
        box.children.add(recentFileDisplay)

        bookmarkBtn.setOnMouseClicked {
            model.bookmarkLastFile()
        }

        uploadBtn.setOnMouseClicked {
            model.uploadLastFile()
        }

        rightHeading.font = Font.font("Verdana", FontWeight.BOLD, 14.0)
        rightBox.padding = Insets(15.0, 12.0, 15.0, 30.0)
        content.children.add(box)
        content.children.add(rightBox)
        return content
    }

    private fun displayFileDetails() {
        val detailHBox = HBox(8.0)
        val shareHBox = HBox(8.0)
        val annotationHBox = HBox(8.0)

        rightHeading.text = detailedCloudFile?.name

        val openBtn = Button("Open file")
        val deleteBtn = Button("Delete file")
        val shareBtn = Button("Share file")
        val drawBtn = Button("Draw")
        val highlightBtn = Button("Highlight")
        val backBtn = Button("Back")
        val shareUsernameField = TextField()
        shareUsernameField.promptText = "Username..."

        openBtn.setOnMouseClicked {
            model.openFileFromCloud(detailedCloudFile!!)
        }

        backBtn.setOnMouseClicked {
            model.updateSideContent(SideContent.VIEW_CLOUD_FILES)
        }

        deleteBtn.setOnMouseClicked {
            model.deleteFileFromCloud(detailedCloudFile!!)
        }

        shareBtn.setOnMouseClicked {
            if (shareUsernameField.text == "") {
                model.displayToastMessage("Must enter username to share with first.", ToastMessage.ERROR)
            } else {
                model.shareFile(detailedCloudFile!!, shareUsernameField.text)
            }
        }

        drawBtn.setOnMouseClicked {
            model.onAnnotation("draw")
        }

        highlightBtn.setOnMouseClicked {
            model.onAnnotation("highlight")
        }

        detailHBox.children.add(rightHeading)
        detailHBox.children.add(backBtn)
        shareHBox.children.add(shareUsernameField)
        shareHBox.children.add(shareBtn)
        annotationHBox.children.add(drawBtn)
        annotationHBox.children.add(highlightBtn)
        rightBox.children.add(detailHBox)
        rightBox.children.add(openBtn)
        rightBox.children.add(deleteBtn)
        rightBox.children.add(annotationHBox)
        rightBox.children.add(shareHBox)

    }

    private fun displayAccountSettings() {
        rightHeading.text = "Enter your new password."
        rightBox.children.add(rightHeading)
        val passwordField = PasswordField()
        passwordField.promptText = "New password..."
        val submitBtn = Button("Update Password")

        submitBtn.setOnMouseClicked {
            val password = passwordField.text
            if (password == "") {
                model.displayToastMessage("Please enter a value before submitting.", ToastMessage.ERROR)
            } else {
                val body = UserPost(model.currentUser?.username!!, password!!)
                model.updateUser(body)
            }
        }

        rightBox.children.add(passwordField)
        rightBox.children.add(submitBtn)
    }

    private fun displaySignInForm() {
        rightHeading.text = if (model.mainSideContent == SideContent.SIGNUP_FORM) "Sign Up" else "Sign In"

        rightBox.children.add(rightHeading)

        val usernameField = TextField()
        usernameField.promptText = "Username..."
        val passwordField = PasswordField()
        passwordField.promptText = "Password..."
        val submitBtn = Button(rightHeading.text)

        submitBtn.setOnMouseClicked {
            val username = usernameField.text
            val password = passwordField.text
            val body = UserPost(username, password)
            if (username != "" && password != "") {
                if (model.mainSideContent == SideContent.SIGNUP_FORM) {
                    model.signUpUser(body)
                } else {
                    model.signInUser(body)
                }
            } else {
                model.displayToastMessage("Please fill in all fields before submitting.", ToastMessage.ERROR)
            }
        }

        rightBox.children.add(usernameField)
        rightBox.children.add(passwordField)
        rightBox.children.add(submitBtn)
    }
    private fun displayHotkeys() {
        rightBox.children.clear()
        rightHeading.text = "Hotkeys"

        rightBox.children.add(rightHeading)
        for ((key, value) in model.shortcut.shortcutMap) {
            val curCommand = HBox(4.0)
            var changeButton = Button("$key")
            changeButton.setOnMouseClicked {
                var newKey = model.shortcut.modify(key)
                if ("$newKey" !== changeButton.text) {
                    displayHotkeys()
                }

            }
            curCommand.children.add(changeButton)
            val text = Text(value)
            text.fill = textColor
            curCommand.children.add(text)
            rightBox.children.add(curCommand)
        }

    }

    private fun displaySideContent() {
        rightBox.children.clear()
        when (model.mainSideContent) {
            SideContent.SIGNIN_FORM -> displaySignInForm()
            SideContent.SIGNUP_FORM -> displaySignInForm()
            SideContent.VIEW_CLOUD_FILES -> displayCloudFiles()
            SideContent.HOTKEYS -> displayHotkeys()
            SideContent.ACCOUNT_SETTINGS -> displayAccountSettings()
            SideContent.CLOUD_FILE_DETAIL -> displayFileDetails()
        }
    }

    override fun updateView() {
        updateTheme()
        displayFiles(model.recentFiles, recentFileDisplay)
        displayFiles(model.bookmarkedFiles, bookmarkedFilesDisplay)
        displaySideContent()
    }

    init {
        view = generateView()
        model.addView(this, ViewString.HOME)
    }

    fun getView(): HBox? {
        return view
    }

}