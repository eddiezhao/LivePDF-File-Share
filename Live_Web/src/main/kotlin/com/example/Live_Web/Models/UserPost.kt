package com.example.Live_Web.Models

/**
 * User data source when posting. (Login)
 */
data class UserPost (
    /**
     * User's username
     */
    val username: String,
    /**
     * User's password.
     */
    val password: String,
)