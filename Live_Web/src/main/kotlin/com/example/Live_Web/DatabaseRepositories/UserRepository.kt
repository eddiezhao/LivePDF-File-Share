package com.example.Live_Web.DatabaseRepositories

import com.example.Live_Web.Models.User
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Mongodb user repository interface
 */
interface UserRepository : MongoRepository<User, String> {
    fun findOneById(id: String): User
    override fun deleteById(id: String): Unit
    fun deleteByUsername(username: String) : Unit
    override fun deleteAll()
    fun findOneByUsername(username: String) : User
}