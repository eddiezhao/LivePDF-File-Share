package com.example.Live_Web.Service

import com.example.Live_Web.Models.File
import org.springframework.stereotype.Service
import com.example.Live_Web.DataSource.FileDataSource
import com.example.Live_Web.Models.FilePost
import com.example.Live_Web.Models.Websocket.DataElement
import org.springframework.web.multipart.MultipartFile

/**
 * File Service Class.
 */
@Service
class FileService (private val dataSource: FileDataSource) {

    /**
     * Retrieves all files.
     */
    fun getFiles(): Collection<File> = dataSource.getFiles()

    /**
     * Retrieves files by id.
     */
    fun getFile(id: String): File = dataSource.getFile(id)

    /**
     * Adds new file.
     */
    fun addFile(file: FilePost): File = dataSource.addFile(file)

    /**
     * Patches existing file
     */
    fun patchFile(file: File): File = dataSource.patchFile(file)

    /**
     * Deletes existing file.
     */
    fun deleteFile(id: String): Unit =  dataSource.deleteFile(id)

    /**
     * Creates/Saves file on system.
     */
    fun createFile(fileId: String, files: MultipartFile?): Unit =  dataSource.createFile(fileId, files)

    /**
     *
     */
    fun getPhysicalFile(fileId: String): ByteArray? = dataSource.getPhysicalFile(fileId)

    /**
     * Shares specified file with specified user.
     */
    fun shareFile(fileId: String, username: String) : Unit = dataSource.shareFile(fileId, username)

    /**
     * Un-shares specified file with specified user.
     */
    fun unShareFile(fileId: String, username: String) : Unit = dataSource.unShareFile(fileId, username)

    /**
     * Retrieves file annotations for file with specified id.
     */
    fun getFileAnnotations(id: String): List<DataElement> = dataSource.getFileAnnotations(id)

}