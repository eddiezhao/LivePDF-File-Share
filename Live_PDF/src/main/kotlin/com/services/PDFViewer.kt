package com.services

import org.jpedal.examples.viewer.Commands
import org.jpedal.examples.viewer.Viewer
import org.jpedal.external.JPedalActionHandler
import org.jpedal.external.Options
import java.awt.BorderLayout
import java.io.File
import java.net.URI
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JPanel

class PDFViewer(file: File) {
    val currentFile = file
    fun openViewer() {
        val frame = JFrame()
        frame.contentPane.layout = BorderLayout()
        val rootContainer = JPanel()
        val viewer = Viewer(rootContainer, null)
        viewer.setupViewer()
        viewer.executeCommand(Commands.OPENFILE, arrayOf<String>(currentFile.absolutePath))

        frame.add(rootContainer, BorderLayout.CENTER)
        frame.title = "LivePDF: " + currentFile.name
        val imageIcon = ImageIcon(System.getProperty("user.dir")+"\\Live_PDF\\lib\\32-clear.png").image
        val listIcons = listOf(imageIcon)
        frame.iconImages = listIcons
        frame.extendedState = frame.extendedState or JFrame.MAXIMIZED_BOTH
        frame.setSize(1200, 800)
        frame.isVisible = true

        val helpAction = JPedalActionHandler { _, _ ->
            java.awt.Desktop.getDesktop().browse(URI("https://git.uwaterloo.ca/m2helwa/cs346_project/-/wikis/Home"))
        }

        val actions: MutableMap<Int, Any> = HashMap<Int, Any>()
        actions[Commands.HELP] = helpAction
        viewer.addExternalHandler(actions, Options.JPedalActionHandler);
    }
}