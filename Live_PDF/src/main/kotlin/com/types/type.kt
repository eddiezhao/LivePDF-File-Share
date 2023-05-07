package com.types

interface IView {
    fun updateView()
}

data class UserPost (
    val username: String,
    val password: String,
)

data class User (
    val username: String,
    val password: String,
    val id: String,
    val filesIdOwned: MutableList<String>,
    val fileIdShared: MutableList<String>
)

data class FilePost (
    val name: String,
    val username: String,
    val password: String,
)

data class CreateFileInstanceResponse (
    val id: String,
    val name: String,
    val userId: String,
    val sharedIds: MutableList<String>,
)

data class SignInResponse (
    val valid: Boolean,
)

data class CloudFile (
    val id: String,
    val name: String,
    val userId: String,
    val sharedIds: MutableList<String>,
)

data class ENV (
    val apiBaseURL: String,
)

data class FileAnnotation (
    val id: String,
    val data: String,
)

