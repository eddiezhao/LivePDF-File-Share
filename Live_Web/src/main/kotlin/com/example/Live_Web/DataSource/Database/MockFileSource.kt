package com.example.Live_Web.DataSource.Database

import com.example.Live_Web.DataSource.FileDataSource
import com.example.Live_Web.DatabaseRepositories.FileRepository
import com.example.Live_Web.DatabaseRepositories.UserRepository
import com.example.Live_Web.Helpers.RandomStrings
import com.example.Live_Web.Models.Exceptions.InvalidLoginException
import com.example.Live_Web.Models.File
import com.example.Live_Web.Models.FilePost
import com.example.Live_Web.Models.User
import com.example.Live_Web.Models.Websocket.DataElement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File as FileSystem


/**
 * User source creation
 */
@Repository
class MockFileSource(
    private val fileRepository: FileRepository,
    private val userRepository: UserRepository
    ): FileDataSource {
    private final val ID_LENGTH = 32

    private final val randomString = RandomStrings();


    override fun getFiles(): Collection<File> {
        return fileRepository.findAll();
    }

    override fun getFile(id : String): File {
        try {
            return fileRepository.findOneById(id)
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a file with ID: $id")
        }
    }

    override fun getFileAnnotations(id: String): List<DataElement> {
        try {
            val file = getFile(id)
            val gson = Gson()

            val path = "../../USER_FILES/${file.userId}/${file.id}.json"
            val jsonText = java.io.File(path).readText(Charsets.UTF_8)
            val typeToken = object : TypeToken<List<DataElement>>() {}.type

            return gson.fromJson(jsonText, typeToken) ?: return listOf()
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a file with ID: $id")
        }
    }

    override fun addFile(file: FilePost): File {
        try {
            val id: String = randomString.getRandomString(ID_LENGTH)

            val user = userRepository.findOneByUsername(file.username)

            if (user.password != file.password) {
                throw InvalidLoginException("Incorrect login.")
            }

            addFileToUser(id, user.id)

            val newFile = File(id, file.name, user.id, mutableListOf())



            fileRepository.save(newFile)

            val propertiesFilePath = "../../USER_FILES/${user.id}/${id}.json"
            val propertiesFile= java.io.File(propertiesFilePath)

            propertiesFile.createNewFile()

            return newFile
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not Create File")
        }
    }
    override fun patchFile(file: File): File {
        try {
            val toUpdateFile = getFile(file.id)
            val newFile = File(toUpdateFile.id, file.name, toUpdateFile.userId, toUpdateFile.sharedIds)

            fileRepository.save(newFile)

            return newFile
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a file with ID: ${file.id}")
        }
    }

    override fun deleteFile(id: String) {
        try {
            val file = fileRepository.findOneById(id)
            val user = userRepository.findOneById(file.userId)
            user.filesIdOwned.remove(id)
            val updatedUser = User(user.id, user.username, user.password, user.filesIdOwned, user.fileIdShared)

            userRepository.save(updatedUser)
            fileRepository.deleteById(id)
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a file with ID: ${id}")
        }

        return
    }

    override fun createFile(fileId: String, multipartFile: MultipartFile?) {
        try {
            val fileObj =  getFile(fileId)

            val file = FileSystem(fileObj.id + ".pdf");

            val initialStream: InputStream = multipartFile?.getInputStream() ?: throw NoSuchElementException("multipartFile?.getInputStream() doesn't exist")
            val buffer = ByteArray(initialStream.available())
            initialStream.read(buffer)

            val path = "../../USER_FILES/${fileObj.userId}/${file}" // REPLACE tmp_file with userId
            FileOutputStream(path).use { outStream -> outStream.write(buffer) }

        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a file with ID: ${fileId}")
        }
    }

    override fun addFileToUser(fileId: String, userId: String) {
        try {
            val user = userRepository.findOneById(userId)


            val newUser = User(user.id,user.username, user.password, user.filesIdOwned, user.fileIdShared)
            newUser.filesIdOwned.add(fileId)

            userRepository.save(newUser)

            return
        } catch (e: Exception) {
            throw InvalidLoginException("No Such user ID.")
        }
    }

    override fun getPhysicalFile(fileId: String): ByteArray? {
        try {
            val fileObj = getFile(fileId)

            val path = Paths.get("../../USER_FILES/${fileObj.userId}/${fileObj.id}.pdf")
            return Files.readAllBytes(path)
        } catch (e: Exception) {
            val fileObj = getFile(fileId)
            throw NoSuchElementException("Could Not fetch file.")
        }

    }

    override fun shareFile(fileId: String, username: String) {
        try {
            val user = userRepository.findOneByUsername(username)
            val file = getFile(fileId)

            val newUser = User(user.id,user.username, user.password, user.filesIdOwned, user.fileIdShared)
            newUser.fileIdShared.add(fileId)

            val newFile= File(file.id, file.name, file.userId, file.sharedIds)
            newFile.sharedIds.add(user.id)

            userRepository.save(newUser)
            fileRepository.save(newFile)

            return
        } catch (e: Exception) {
            throw InvalidLoginException("No Such file ID or username.")
        }
    }

    override fun unShareFile(fileId: String, username: String) {
        try {
            val user = userRepository.findOneByUsername(username)
            val file = getFile(fileId)


            val newUser = User(user.id,user.username, user.password, user.filesIdOwned, user.fileIdShared)
            newUser.fileIdShared.remove(fileId)

            val newFile= File(file.id, file.name, file.userId, file.sharedIds)
            newFile.sharedIds.remove(user.id)

            userRepository.save(newUser)
            fileRepository.save(newFile)
            return
        } catch (e: Exception) {
            throw InvalidLoginException("No Such file ID or username.")
        }
    }
}