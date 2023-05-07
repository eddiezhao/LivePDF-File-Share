package com.example.Live_Web.Controllers

import com.example.Live_Web.Models.User
import com.example.Live_Web.Models.UserPost
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jsonMapper
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import kotlin.test.BeforeTest
import kotlin.test.Ignore

@SpringBootTest
@AutoConfigureMockMvc
internal class UserControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {
    val BASE_URL = "/api/user"

    val USERNAME = "Mickey_Rat"
    val PASSWORD = "DonaldTrumpsSafe123"
    var ID = "nvd36ii2eK35GERKKdYde7qutyVJE93s" // CHANGE THIS

    @Test
    fun `Should Crete user` () {
        val userPost = UserPost(USERNAME, "Door2Darkness")
        mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(userPost)
        }
            .andDo{ print() }
            .andExpect {
                status { isCreated() }
            }

        val json = mockMvc.get("$BASE_URL/username/${userPost.username}")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.username") {value(USERNAME)}
                jsonPath("$.password") {value("Door2Darkness")}
            }
            .andReturn ()
        ID = JSONObject(json.getResponse().getContentAsString())["id"] as String
        print(ID)
    }

    @Test
    fun `Should Patch a User` () {
        val userPost = UserPost(USERNAME, PASSWORD)
        mockMvc.patch(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(userPost)
        }
            .andDo{ print() }
            .andExpect {
                status { isOk() }
            }

        mockMvc.get("$BASE_URL/username/${userPost.username}")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") {value(ID)}
                jsonPath("$.username") {value(USERNAME)}
                jsonPath("$.password") {value(PASSWORD)}
            }

    }

    @Test
    fun `Should return user with ID` () {
        val id = ID
        mockMvc.get("$BASE_URL/id/$id")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.username") {value(USERNAME)}
                jsonPath("$.password") {value(PASSWORD)}
            }

    }

    @Test
    fun `Should return user with Username` () {
        val username = USERNAME
        mockMvc.get("$BASE_URL/username/$username")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") {value(ID)}
                jsonPath("$.password") {value(PASSWORD)}
            }

    }


    @Test
    fun `User ID doesn't exist and returns not found` () {
        val id = "NoSuchID"
        mockMvc.get("$BASE_URL/id/$id")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `User username doesn't exist and returns not found` () {
        val username = "NoSuchUsername"
        mockMvc.get("$BASE_URL/username/$username")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }
    }



    @Test
    fun `Should Delete a User by username` () {
        val username = USERNAME
        mockMvc.delete("$BASE_URL/username/$username")
            .andDo{ print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$BASE_URL/username/${username}")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }

    }

    @Test
    fun `Should validate valid user` () {
        val user = UserPost("TestUSer", "TestPassword")
        mockMvc.post("$BASE_URL/verify")
            {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(user)
            }
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.valid") {value(true)}
            }
    }

    @Test
    fun `Should validate invalid user (username)` () {
        val user = UserPost("NotAnFBISpy", "TestPassword")
        mockMvc.get("$BASE_URL/verify")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.valid") {value(false)}
            }
    }

    @Test
    fun `Should validate invalid user (password)` () {
        val user = UserPost("TestUSer", "FBI OPEN UP")
        mockMvc.get("$BASE_URL/verify")
        {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(user)
        }
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.valid") {value(false)}
            }
    }

    @Test
    fun `Should get files of users` () {
        mockMvc.get("$BASE_URL/getFiles/TestUSer")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].id") {value("rcMGpCGcADJ0CTaDtIh1pwDFRE3J4UEK")}
                jsonPath("$[0].name") {value("file.pdf")}
            }
    }

    @Test
    fun `Should get shared files of users` () {
        mockMvc.get("$BASE_URL/shared/Tw1YmkTVtrdCTAQxaZJAyni0mVQaBZf1")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].id") {value("rcMGpCGcADJ0CTaDtIh1pwDFRE3J4UEK")}
                jsonPath("$[0].name") {value("file.pdf")}
            }
    }

    @Test
    @Ignore
    fun `Should Delete a User by id` () {
        val id = "UserID123"
        mockMvc.delete("$BASE_URL/id/$id")
            .andDo{ print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$BASE_URL/id/${id}")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }

    }

    @Test
    @Ignore
    fun `User ID doesn't exist and returns not found when deleting` () {
        val id = "NoSuchID"
        mockMvc.delete("$BASE_URL/id/$id")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @Ignore
    fun `User username doesn't exist and returns not found when deleting` () {
        val username = "NoSuchUsername"
        mockMvc.delete("$BASE_URL/username/$username")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }
    }
}