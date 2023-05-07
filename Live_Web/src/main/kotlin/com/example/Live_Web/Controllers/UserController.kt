package com.example.Live_Web.Controllers

import com.example.Live_Web.Models.*
import com.example.Live_Web.Models.Exceptions.InvalidLoginException
import com.example.Live_Web.Service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * API Controller for User Data Structure.
 */
@RestController
@RequestMapping("api/user")
class UserController (private val service: UserService) {

    /**
     * Exception handler for 'NoSuchElementException'
     */
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException) : ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    /**
     * Exception handler for 'InvalidLoginException'
     */
    @ExceptionHandler(InvalidLoginException::class)
    fun handleInvalidLogin(e: NoSuchElementException) : ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NO_CONTENT)

    /**
     * GET endpoint that retrieves user by ID.
     */
    @GetMapping("/id/{id}")
    fun returnUserId(@PathVariable id: String): User {
        return service.getUserId(id)
    }

    /**
     * GET endpoint that verifies user login.
     */
    @PostMapping("/verify")
    fun verifyUser(@RequestBody userBody: UserPost): Verify {
        return service.verifyUser(userBody)
    }
    /**
     * GET endpoint that retrieves user by username.
     */
    @GetMapping("/username/{username}")
    fun returnUserUsername(@PathVariable username: String): User {
        return service.getUserUsername(username)
    }

    /**
     * GET endpoint that retrieves list of files for specified user.
     */
    @GetMapping("/getFiles/{username}")
    fun returnUserFiles(@PathVariable username: String): List<File> {
        return service.getUserFiles(username)
    }

    /**
     * POST endpoint that creates a user.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postUser(@RequestBody userBody: UserPost): Unit {
        return service.addUser(userBody)
    }

    /**
     * PATCH endpoint that patches existing user.
     */
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    fun patchUser(@RequestBody userBody: UserPost): Unit {
        return service.patchUser(userBody)
    }

    /**
     *  DELETE endpoint that removes user by id.
     */
    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUserId(@PathVariable id: String): Unit {
        return service.deleteUserId(id)
    }

    /**
     *  DELETE endpoint that removes user by username.
     */
    @DeleteMapping("/username/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUserUsername(@PathVariable username: String): Unit {
        return service.deleteUserUsername(username)
    }

    @GetMapping("/shared/{userId}")
    @ResponseStatus(HttpStatus.OK)
    fun returnSharedFiles(@PathVariable userId: String) :List<File> {
        return service.returnSharedFiles(userId)
    }
}