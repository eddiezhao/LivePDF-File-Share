package com.services

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.types.*
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

class APIClient {
    private val client = HttpClient.newBuilder().build()
    private val gson = Gson()

    private val s3URL = "https://watwars-1.s3.amazonaws.com/env.json"
    private var baseURL = "http://localhost:9000/"
    private var userURL = baseURL + "api/user"
    private var fileURL = baseURL + "api/files"

    init {
        val request = HttpRequest.newBuilder().uri(URI.create(s3URL)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val data = gson.fromJson(response.body(), ENV::class.java)
        baseURL = data.apiBaseURL
        userURL = baseURL + "api/user"
        fileURL = baseURL + "api/files"
        println(baseURL)
    }

    fun isSuccess(statusCode: Int): Boolean {
        return statusCode in 200..299
    }

    fun signUpUser(user: UserPost): Boolean {
        val requestURL = userURL
        val jsonString = gson.toJson(user)
        val request = HttpRequest.newBuilder()
            .uri(URI.create(requestURL))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(jsonString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return isSuccess(response.statusCode())
    }

    fun signInUser(user: UserPost): Boolean {
        val requestURL = "$userURL/verify"
        val jsonString = gson.toJson(user)
        val request = HttpRequest.newBuilder()
            .uri(URI.create(requestURL))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(jsonString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (!isSuccess(response.statusCode())) {
            return false
        }
        val data = gson.fromJson(response.body(), SignInResponse::class.java)
        return data.valid
    }

    fun getUserByUsername(username: String): User {
        val requestURL = "$userURL/username/$username"
        val request = HttpRequest.newBuilder().uri(URI.create(requestURL)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return gson.fromJson(response.body(), User::class.java)
    }

    fun getUserByUserId(userId: String): User {
        val requestURL = "$userURL/id/$userId"
        val request = HttpRequest.newBuilder().uri(URI.create(requestURL)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return gson.fromJson(response.body(), User::class.java)
    }

    fun createFileInstance(file: File, user: User): String? {
        val requestURL = fileURL
        val filePost = FilePost(file.name, user.username, user.password)
        val jsonString = gson.toJson(filePost)
        val request = HttpRequest.newBuilder()
            .uri(URI.create(requestURL))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(jsonString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (!isSuccess(response.statusCode())) {
            return null
        }
        val data = gson.fromJson(response.body(), CreateFileInstanceResponse::class.java)
        return data.id
    }

    fun uploadFile(file: File, fileID: String): Boolean {
        val requestURL = "$fileURL/upload/$fileID"
        val multipart = MultiPartBodyPublisher(requestURL, "UTF-8")
        multipart.addFilePart("file", file)
        val responseCode = multipart.finish()
        return responseCode == HttpURLConnection.HTTP_CREATED
    }

    fun getFilesByUsername(username: String): ArrayList<CloudFile> {
        var requestURL = "$userURL/getFiles/$username"
        val request = HttpRequest.newBuilder().uri(URI.create(requestURL)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val listType = object : TypeToken<ArrayList<CloudFile>>() {}.type
        return gson.fromJson(response.body(), listType)
    }

    fun getSharedFileByUserID(userID: String): ArrayList<CloudFile> {
        var requestURL = "$userURL/shared/$userID"
        val request = HttpRequest.newBuilder().uri(URI.create(requestURL)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val listType = object : TypeToken<ArrayList<CloudFile>>() {}.type
        return gson.fromJson(response.body(), listType)
    }

    fun downloadFile(fileID: String): InputStream? {
        var requestURL = "$fileURL/download/$fileID"
        val url = URL(requestURL)
        val httpConn = url.openConnection() as HttpURLConnection
        val respCode = httpConn.responseCode
        if (!isSuccess(respCode)) {
            return null
        }

        return httpConn.inputStream
    }

    fun patchUser(user: UserPost): Boolean {
        val jsonString = gson.toJson(user)
        val requestURL = userURL
        val request = HttpRequest.newBuilder()
            .uri(URI.create(requestURL))
            .header("Content-Type", "application/json")
            .PUT(BodyPublishers.ofString(jsonString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return isSuccess(response.statusCode())
    }

    fun deleteFile(fileID: String): Boolean {
        val requestURL = "$fileURL/$fileID"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(requestURL))
            .header("Content-Type", "application/json")
            .DELETE()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return isSuccess(response.statusCode())
    }

    fun shareFile(fileID: String, username: String): Boolean{
        val requestURL = "$fileURL/share/$fileID/with/$username"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(requestURL))
            .header("Content-Type", "application/json")
            .PUT(BodyPublishers.ofString(""))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return isSuccess(response.statusCode())
    }

    fun getAnnotationsByFileID(fileID: String): ArrayList<FileAnnotation> {
        val requestURL = "$fileURL/annotations/$fileID"
        val request = HttpRequest.newBuilder().uri(URI.create(requestURL)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val listType = object : TypeToken<ArrayList<FileAnnotation>>() {}.type
        return gson.fromJson(response.body(), listType)
    }
}
