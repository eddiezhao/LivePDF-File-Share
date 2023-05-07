package com.example.Live_Web.Models

/**
 * User Data source when adding file.
 */
data class UserAddFile (
    /**
     * Files id.
     */
    val fileId : String,

    /**
     * Users username
     */
    val username: String,

    /**
     * Users Password
     */
    val password: String,
)