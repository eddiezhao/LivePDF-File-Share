package com.example.Live_Web.Models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * User DataSource.
 */
@Document
data class User (

    /**
     * Users id.
     */
    @Id
    val id : String,

    /**
     * Users username
     */
    @Indexed(unique = true)
    val username: String,

    /**
     * Users Password
     */
    val password: String,

    /**
     * File ID's owned by user.
     */
    val filesIdOwned: MutableList<String>,

    /**
     *
     */
    val fileIdShared: MutableList<String>
    )