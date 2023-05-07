package com.example.Live_Web.DataSource.Database

import com.example.Live_Web.DataSource.UserDataSource
import com.example.Live_Web.DatabaseRepositories.FileRepository
import com.example.Live_Web.DatabaseRepositories.UserRepository
import com.example.Live_Web.Helpers.RandomStrings
import com.example.Live_Web.Models.Exceptions.InvalidLoginException
import com.example.Live_Web.Models.File
import com.example.Live_Web.Models.User
import com.example.Live_Web.Models.UserPost
import com.example.Live_Web.Models.Verify
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Paths

@Repository
class MockUserSource (
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
): UserDataSource  {
    private final val ID_LENGTH = 32

    private final val randomString = RandomStrings()

    override fun addUser(user: UserPost) {
        try {
            val id: String = randomString.getRandomString(ID_LENGTH)
            val newUser = User(id, user.username, user.password, mutableListOf(), mutableListOf())

            userRepository.save(newUser)

            val path = "../../USER_FILES/${id}"
            Files.createDirectories(Paths.get(path))

            return
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not Create User.")
        }

    }

    override fun deleteUserId(id: String) {
        try {
            userRepository.deleteById(id)
            return
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a user with ID: $id")
        }

    }

    override fun deleteUserUsername(username: String) {
        try {
            userRepository.deleteByUsername(username)
            return
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a user with username: $username")
        }
    }

    override fun getUserId(id: String): User {
        try {
            return userRepository.findOneById(id)

        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a user with ID: $id")
        }
    }

    override fun getUserUsername(userName: String): User {
        try {
            return userRepository.findOneByUsername(userName)

        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a user with username: $userName")
        }
    }

    override fun patchUser(user: UserPost) {
        try {
            val toUpdateUser = getUserUsername(user.username)
//            if (toUpdateUser.password != user.password) {
//                throw InvalidLoginException("Incorrect login.")
//            }

            val newUser = User(toUpdateUser.id, user.username, user.password, toUpdateUser.filesIdOwned, toUpdateUser.filesIdOwned)
            userRepository.save(newUser)

            return
        } catch (e: Exception) {
            throw InvalidLoginException("Incorrect login.")
        }
    }


    override fun verifyUser(user: UserPost): Verify {
        try {
            val userLogin = getUserUsername(user.username)
            if (user.password == userLogin.password) {
                return Verify(true)
            }
            return Verify(false)
        } catch (e: Exception) {
            return Verify(valid = false)
        }
    }

    override fun getUserFiles(username: String): List<File> {
        try {
            val user = getUserUsername(username)
            var files = mutableListOf<File>()

            for (id in user.filesIdOwned) {
                files.add(fileRepository.findOneById(id))
            }

            return files
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a user with username: $username")
        }
    }

    override fun returnSharedFiles(userId: String): List<File> {
        try {
            val user = getUserId(userId)
            var files = mutableListOf<File>()

            for (id in user.fileIdShared) {
                files.add(fileRepository.findOneById(id))
            }

            return files
        } catch (e: Exception) {
            throw NoSuchElementException("Could Not find a user with userId: $userId")
        }
    }

}