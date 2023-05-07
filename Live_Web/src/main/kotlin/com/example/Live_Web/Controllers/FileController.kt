package com.example.Live_Web.Controllers

import com.example.Live_Web.Models.File
import com.example.Live_Web.Models.FilePost
import com.example.Live_Web.Models.Websocket.DataElement
import com.example.Live_Web.Service.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


/**
 * API Controller for File Data Structure.
 */
@RestController
@RequestMapping("api/files")
class FileController (private val service: FileService) {

    /**
     * Exception handler for 'NoSuchElementException'
     */
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException) : ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    /**
     *  GET endpoint that returns list of files.
     */
    @GetMapping
    fun returnFile(): Collection<File> {
        return service.getFiles()
    }


    /**
     * GET endpoint the gets file by ID.
     */
    @GetMapping("/{id}")
    fun getFile(@PathVariable id: String) : File {
        return service.getFile(id)
    }

    /**
     * GET endpoint the gets file annotations by ID.
     */
    @GetMapping("/annotations/{id}")
    fun getFileAnnotations(@PathVariable id: String) : List<DataElement> {
        return service.getFileAnnotations(id)
    }

    /**
     * POST endpoint that creates a new file.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postFile(@RequestBody filePost: FilePost): File {
        return service.addFile(filePost)
    }

    /**
     * POST endpoint that uploads Multipart file.
     */
    @PostMapping("/upload/{fileId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadFile(@PathVariable fileId: String, @RequestPart("file")file: MultipartFile?): Unit {
        return service.createFile(fileId, file)
    }

    /**
     * Responds with pdf file with given file ID.
     */
    @GetMapping(value = ["/download/{fileId}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @ResponseBody
    fun getPhysicalFile(@PathVariable fileId: String): ByteArray? {
        return service.getPhysicalFile(fileId)
    }

    /**
     *  PATCh endpoint that patches existing file.
     */
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    fun patchFile(@RequestBody filePost: File): File {
        return service.patchFile(filePost)
    }

    /**
     * DELETE endpoint that removes existing file.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFile(@PathVariable id: String): Unit {
        service.deleteFile(id)
    }
    /**
     * POST endpoint that shares the file with specified id to user with specified username
     */
    @PutMapping("/share/{fileId}/with/{username}")
    @ResponseStatus(HttpStatus.OK)
    fun shareFile(@PathVariable fileId: String, @PathVariable username: String) : Unit {
        service.shareFile(fileId, username)
    }

    /**
     * POST endpoint that shares the file with specified id to user with specified username
     */
    @PutMapping("/unshare/{fileId}/with/{username}")
    @ResponseStatus(HttpStatus.OK)
    fun unShareFile(@PathVariable fileId: String, @PathVariable username: String) : Unit {
        service.unShareFile(fileId, username)
    }

}