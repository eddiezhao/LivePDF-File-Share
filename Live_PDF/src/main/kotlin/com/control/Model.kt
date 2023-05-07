package com.control

import com.WebsocketClient.WebsocketSetup
import com.services.APIClient
import com.services.PDFViewer
import com.services.Toast
import com.services.Chat
import com.types.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File
import java.io.FileOutputStream
import com.google.gson.Gson

fun writeToFile(content: ArrayList<String>, filePath: String) {
    val file = File(filePath)
    file.printWriter().use { out ->
        for (c in content) {
            out.println(c)
        }
    }
}
fun createFile(filePath: String) {
    var home = System.getProperty("user.home")
    val f = File(home + "/Live_PDF/src/main/kotlin/assets/")
    if (!f.exists()) {
        f.mkdirs()
    }
    val ok = File(filePath).createNewFile()
    if (!ok) {
        println("Error creating file $filePath")
    }
}

fun loadContent(filePath: String) : File? {
    return try {
        val fileContent = File(filePath)
        if (!fileContent.exists()) {
            throw Exception()
        }
        fileContent
    } catch (e: Exception) {
        createFile(filePath)
        loadContent(filePath)
    }
}

fun loadFromFile(filePath: String, store_location: ArrayList<String>) {
    loadContent(filePath)?.forEachLine {
        store_location.add(it)
    }
}

class Model(stage: Stage) {
    private val gson = Gson()
    private var socketClient = WebsocketSetup()
    private val apiClient = APIClient()
    private val stage = stage
    private val maxRecentFiles = 10
    private val home = System.getProperty("user.home")
    private val path = home + "/Live_PDF/src/main/kotlin/assets/"
    private val recentFilePath =  path + "recent.txt"
    private val bookmarkedFilePath = path + "bookmarked.txt"
    private val cloudFilePath = path + "cloud.pdf"
    private val annotationsFilePath = path + "annotations.json"
    private var lastOpenedFile: String? = null
    private val views = mutableMapOf<ViewString, IView>()
    private var chat : Chat? = null

    var recentFiles: ArrayList<String> = ArrayList()
    var bookmarkedFiles: ArrayList<String> = ArrayList()
    var cloudFiles: ArrayList<CloudFile> = ArrayList()
    var sharedFiles: ArrayList<CloudFile> = ArrayList()
    var currentUser: User? = null
    var currentTheme: Theme = Theme.LIGHT
    var mainSideContent: SideContent = SideContent.NONE
    val shortcut = ShortcutHandler(this)
    private val toast = Toast(stage)


    private val fileChooser = FileChooser()
    private val extensionFilter = FileChooser.ExtensionFilter("PDF", "*.pdf")
    fun addView(view: IView, key: ViewString) {
        views[key] = view
        views[key]?.updateView()
    }

    private fun addToRecent(file: File) {
        val path = file.absolutePath
        val pathIdx = recentFiles.indexOf(path)
        if (pathIdx != -1) {
            recentFiles.removeAt(pathIdx)
        }
        recentFiles.add(0, path)
        if (recentFiles.size > maxRecentFiles) {
            recentFiles.removeAt(recentFiles.size - 1)
        }
        views[ViewString.HOME]?.updateView()
        writeToFile(recentFiles, recentFilePath)
    }

    fun openFile(file: File, saveToRecent: Boolean = true) {
        lastOpenedFile = file.absolutePath
        if (saveToRecent) {
            addToRecent(file)
        }
        PDFViewer(file).openViewer()
    }

    fun bookmarkLastFile() {
        if (lastOpenedFile == null) {
            return
        }
        if (lastOpenedFile in bookmarkedFiles) {
            return
        }
        bookmarkedFiles.add(lastOpenedFile!!)
        views[ViewString.HOME]?.updateView()
        writeToFile(bookmarkedFiles, bookmarkedFilePath)
    }

    private fun setUser(user: User?) {
        currentUser = user
        mainSideContent = SideContent.NONE
        views[ViewString.MENUBAR]?.updateView()
        views[ViewString.HOME]?.updateView()
        chat = currentUser?.let { Chat(it.username, socketClient) }
    }

    fun openLastFile() {
        if (lastOpenedFile == null) {
            if (recentFiles.size > 0) {
                openFile(File(recentFiles.first()))
            }
            return
        }
        openFile(File(lastOpenedFile))
    }
    fun openFileMenu() {
        var file = fileChooser.showOpenDialog(stage)
        if (file != null) {
            openFile(file)
        }
    }

    fun switchTheme() {
        currentTheme = if (currentTheme == Theme.LIGHT) Theme.DARK else Theme.LIGHT
        views[ViewString.HOME]?.updateView()
        views[ViewString.SIDEBAR]?.updateView()
    }

    fun signUpUser(body: UserPost) {
        val isSuccess = apiClient.signUpUser(body)
        if (isSuccess) {
            val user = apiClient.getUserByUsername(body.username)
            setUser(user)
            displayToastMessage("Successfully signed up!", ToastMessage.SUCCESS)
        } else {
            displayToastMessage("Error signing up, please try again later", ToastMessage.ERROR)
        }
    }

    fun signInUser(body: UserPost) {
        val isValid = apiClient.signInUser(body)
        if (isValid) {
            val user = apiClient.getUserByUsername(body.username)
            setUser(user)
            displayToastMessage("Successfully signed in!", ToastMessage.SUCCESS)
        } else {
            displayToastMessage("Error signing in, please confirm you used the correct credentials", ToastMessage.ERROR)
        }
    }

    fun signOutUser() {
        setUser(null)
        cloudFiles.clear()
        sharedFiles.clear()
        socketClient = WebsocketSetup()
        displayToastMessage("Successfully signed out!", ToastMessage.SUCCESS)
    }

    fun updateUser(body: UserPost) {
        val isSuccess = apiClient.patchUser(body)
        if (isSuccess) {
            val newUser = User(currentUser?.username!!, body.password, currentUser?.id!!, currentUser?.filesIdOwned!!, currentUser?.fileIdShared!!)
            setUser(newUser)
            displayToastMessage("Password successfully updated", ToastMessage.SUCCESS)
        } else {
            displayToastMessage("Error updating password, please try again later.", ToastMessage.ERROR)
        }
    }

    fun uploadLastFile() {
        if (lastOpenedFile == null) {
            displayToastMessage("You do not have any last opened file.", ToastMessage.ERROR)
            return
        }
        if (currentUser == null) {
            displayToastMessage("You need to be signed in to upload files to your cloud.", ToastMessage.ERROR)
            return
        }
        val fileToUpload = File(lastOpenedFile)
        val fileID = apiClient.createFileInstance(fileToUpload, currentUser!!)
        if (fileID == null) {
            displayToastMessage("Error creating file instance. Please try again later.", ToastMessage.ERROR)
            return
        }
        val isSuccess = apiClient.uploadFile(fileToUpload, fileID)
        if (isSuccess) {
            displayToastMessage("${fileToUpload.name} has been successfully uploaded to your cloud", ToastMessage.SUCCESS)
        } else {
            displayToastMessage("Error uploading file, please try again later", ToastMessage.ERROR)
        }
    }

    fun openCloudFiles() {
        if (currentUser == null) {
            displayToastMessage("You must be signed in to view your cloud files.", ToastMessage.ERROR)
            return
        }
        cloudFiles = apiClient.getFilesByUsername(currentUser?.username!!)
        sharedFiles = apiClient.getSharedFileByUserID(currentUser?.id!!)
        mainSideContent = SideContent.VIEW_CLOUD_FILES
        views[ViewString.HOME]?.updateView()
    }

    fun openFileFromCloud(file: CloudFile) {
        val inputStream = apiClient.downloadFile(file.id)
        val outputStream = FileOutputStream(cloudFilePath)
        var bytesRead: Int
        val buffer = ByteArray(4096)
        while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        outputStream.close()
        inputStream!!.close()

        val annotations = apiClient.getAnnotationsByFileID(file.id)
        println(annotations)
        val annotationsFile = File(annotationsFilePath)
        annotationsFile.writeText(gson.toJson(annotations))
        chat?.chatInit(stage)
        if (socketClient.used()) {
            socketClient.close()
        }
        socketClient.setup(file.id, currentUser?.id!!, file.userId, chat!!)
        openFile(File(cloudFilePath), false)

    }

    fun deleteFileFromCloud(file: CloudFile) {
        val isSuccess = apiClient.deleteFile(file.id)
        if (isSuccess)  {
            displayToastMessage("${file.name} has been successfully deleted from your cloud", ToastMessage.SUCCESS)
        } else {
            displayToastMessage("Error, please try again later", ToastMessage.ERROR)
        }
        cloudFiles = apiClient.getFilesByUsername(currentUser?.username ?: "")
        mainSideContent = SideContent.VIEW_CLOUD_FILES
        views[ViewString.HOME]?.updateView()
    }

    fun updateSideContent(newView: SideContent) {
        mainSideContent = newView
        views[ViewString.HOME]?.updateView()
    }

    fun onAnnotation(action: String) {
        socketClient.sendModification(0, "", action)
    }

//    fun openChat() {
//        chat.chatInit(stage)
//    }

    fun displayToastMessage(message: String, type: ToastMessage = ToastMessage.SUCCESS) {
        toast.makeText(message, type)
    }

    fun shareFile(file: CloudFile, username: String) {
        val isSuccess = apiClient.shareFile(file.id, username)
        if (isSuccess) {
            displayToastMessage("Successfully shared ${file.name} with $username", ToastMessage.SUCCESS)
        } else {
            displayToastMessage("Error sharing file, please try again later", ToastMessage.ERROR)
        }
    }

    init {
        loadFromFile(recentFilePath, recentFiles)
        loadFromFile(bookmarkedFilePath, bookmarkedFiles)
        fileChooser.extensionFilters.add(extensionFilter)
        shortcut.init()
    }
}