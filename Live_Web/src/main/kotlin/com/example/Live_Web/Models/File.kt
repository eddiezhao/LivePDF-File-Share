package com.example.Live_Web.Models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


/**
 * File Data Source.
 */
@Document
data class File (
    /**
     * ID of file.
     */
    @Id
    val id: String,
    /**
     *  File name.
     */
    val name: String,

    /**
     *  Users ID who owns file.
     */
    val userId: String,

    /**
     * Users this file is shared with.
     */
    val sharedIds: MutableList<String>

)