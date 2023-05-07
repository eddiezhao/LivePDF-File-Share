package com.example.Live_Web.DataSource

import com.example.Live_Web.Models.File
import com.example.Live_Web.Models.FilePost
import com.example.Live_Web.Models.UserAddFile
import com.example.Live_Web.Models.Websocket.DataElement
import org.springframework.web.multipart.MultipartFile

/**
 * Abstract Interface for File data source.
 */
interface FileDataSource {
    /**
     *  Retrieves all files.
     */
    fun getFiles() : Collection<File>

    /**
     * Retrieves files by id.
     */
    fun getFile(id: String) : File

    /**
     *  Adds new file.
     */
    fun addFile(file: FilePost): File

    /**
     * Patches existing file
     */
    fun patchFile(file: File): File

    /**
     * Deletes existing file.
     */
     fun deleteFile(id: String) : Unit

    /**
     * Creates/Saves file on system.
     */
    fun createFile(fileId: String, files: MultipartFile?): Unit

    /**
     * Associates file id by user.
     */
    fun addFileToUser(fileId: String, userId: String): Unit

    /**
     * Returns byte array of file with given ID
     */
    fun getPhysicalFile(fileId: String): ByteArray?

    /**
     * Shares specified file with specified user.
     */
    fun shareFile(fileId: String, username: String) : Unit

    /**
     * Un-shares specified file with specified user.
     */
    fun unShareFile(fileId: String, username: String) : Unit

    /**
     * Retrieves file annotations for file with specified id.
     */
    fun getFileAnnotations(id: String): List<DataElement>
}