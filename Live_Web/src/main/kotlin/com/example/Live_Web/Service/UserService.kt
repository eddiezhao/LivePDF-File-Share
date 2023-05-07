package com.example.Live_Web.Service

import com.example.Live_Web.DataSource.UserDataSource
import com.example.Live_Web.Models.*
import org.springframework.stereotype.Service

/**
 * User Service Class.
 */
@Service
class UserService (private val dataSource: UserDataSource) {
    /**
     * Gets user by id.
     */
    fun getUserId(id: String): User = dataSource.getUserId(id)

    /**
     * Gets user by username.
     */
    fun getUserUsername(userName: String): User = dataSource.getUserUsername(userName)

    /**
     * Adds user.
     */
    fun addUser(user: UserPost): Unit = dataSource.addUser(user)

    /**
     * Patches existing user by username (Updates password).
     */
    fun patchUser(user: UserPost): Unit = dataSource.patchUser(user)

    /**
     * Removes User by id.
     */
    fun deleteUserId(id: String): Unit = dataSource.deleteUserId(id)

    /**
     * Removes user by username.
     */
    fun deleteUserUsername(username: String): Unit = dataSource.deleteUserUsername(username)

    /**
     * Verify user login.
     */
    fun verifyUser(user: UserPost): Verify = dataSource.verifyUser(user)

    /**
     * Retrieves list of files for specified user.
     */
    fun getUserFiles(username: String): List<File> = dataSource.getUserFiles(username)

    /**
     * Retrieves list of shared files for specified user.
     */
    fun returnSharedFiles(userId: String) :List<File> = dataSource.returnSharedFiles(userId)

}