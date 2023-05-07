package com.example.Live_Web.DataSource

import com.example.Live_Web.Models.*

/**
 * Abstract Interface for User data source.
 */
interface UserDataSource {
    /**
     * Gets user by id.
     */
    fun getUserId(id: String): User

    /**
     * Gets user by username.
     */
    fun getUserUsername(userName: String): User

    /**
     * Adds user.
     */
    fun addUser(user: UserPost): Unit

    /**
     * Patches existing user by username (Updates password).
     */
    fun patchUser(User: UserPost): Unit

    /**
     * Removes User by id.
     */
    fun deleteUserId(id: String): Unit

    /**
     * Removes user by username.
     */
    fun deleteUserUsername(username: String): Unit

    /**
     * Verify user login.
     */
    fun verifyUser(user: UserPost): Verify

    /**
     * Retrieves list of files for specified user.
     */
    fun getUserFiles(username: String): List<File>

    /**
     * Retrieves list of shared files for specified user.
     */
    fun returnSharedFiles(userId: String): List<File>
}