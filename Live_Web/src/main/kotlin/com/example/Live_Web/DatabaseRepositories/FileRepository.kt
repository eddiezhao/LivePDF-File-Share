package com.example.Live_Web.DatabaseRepositories

import com.example.Live_Web.Models.File
import org.bson.types.ObjectId
import org.springframework.data.domain.Example
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

/**
 * Mongodb file repository interface
 */
interface FileRepository : MongoRepository<File, String> {
    fun findOneById(id: String): File
    override fun deleteById(id: String) : Unit
    override fun deleteAll()

}