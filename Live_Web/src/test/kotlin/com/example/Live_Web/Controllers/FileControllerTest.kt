package com.example.Live_Web.Controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import com.example.Live_Web.Models.File
import com.example.Live_Web.Models.FilePost
import org.springframework.test.web.servlet.*
import kotlin.test.Ignore

@SpringBootTest
@AutoConfigureMockMvc
internal class FileControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper
) {

    val BASE_URL = "/api/files"
    val id = "EKUxqipz2Nc2ZsvS7vmOIYrTf1nu6uQ8" // CHANGE THIS

    val USERNAME = "TestUSer"
    val PASSWORD = "TestPassword"
    @Test
    fun `Post to add new file` () {
        val newFile = FilePost("newFile.pdf", USERNAME, PASSWORD)
        mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newFile)
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.name") {value("newFile.pdf")}
                }
            }
    }

    @Test
    // @Ignore
    fun `Should return endpoints` () {
        mockMvc.get(BASE_URL)
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].name") {value("newFile.pdf")}
            }

    }

    @Test
    fun `Get one File with given ID` () {
        val fileID = id
        mockMvc.get("$BASE_URL/$fileID")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.name") {value("newFile.pdf")}
                }
            }

    }

    @Test
    fun `Get one File Annotation with given ID` () {
        val fileID = id
        mockMvc.get("$BASE_URL/annotations/obn8FR0xqtzUAgD2vRk1cYjIlbQrHBFp")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$[0].id") {value("OJfryZ93A2p3awHa")}
                }
            }

    }

    @Test
    fun `File doesn't exist and returns not found` () {
        val fileId = "NoSuchID"
        mockMvc.get("$BASE_URL/$fileId")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `Update existing file`() {
        val updatedFile = File(id, "Updated.pdf", "bz80Eyw56IFLDdagQNG5IBI2Cw0TE1LV", mutableListOf())

        val preformPatch = mockMvc.patch(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(updatedFile)
        }

        preformPatch.andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(updatedFile))
                }
            }
        mockMvc.get(BASE_URL + "/${updatedFile.id}")
            .andExpect {
                content {
                    json(objectMapper.writeValueAsString(updatedFile))
                }
            }
    }

    @Test
    fun `Update Non-existing file`() {
        val invalidFile = File("WhatId","NoSuchFile.pdf", "tmp", mutableListOf())

        val preformPatch = mockMvc.patch(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidFile)
        }

        preformPatch
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `Share File` () {
        mockMvc.patch("$BASE_URL/share/rcMGpCGcADJ0CTaDtIh1pwDFRE3J4UEK/with/friendUser")
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }
        mockMvc.get("$BASE_URL/rcMGpCGcADJ0CTaDtIh1pwDFRE3J4UEK")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.sharedIds[0]") {value("Tw1YmkTVtrdCTAQxaZJAyni0mVQaBZf1")}
                }
            }
        mockMvc.get("/api/user/username/friendUser")
            .andDo{ print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.fileIdShared[0]") {value("rcMGpCGcADJ0CTaDtIh1pwDFRE3J4UEK")}
                }
            }
    }

    @Ignore
    @Test
    fun `un-share File` () {
        mockMvc.patch("$BASE_URL/unshare/rcMGpCGcADJ0CTaDtIh1pwDFRE3J4UEK/with/friendUser")
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }

//        mockMvc.get("$BASE_URL/rcMGpCGcADJ0CTaDtIh1pwDFRE3J4UEK")
//            .andDo{ print() }
//            .andExpect {
//                status { isOk() }
//                content {
//                    contentType(MediaType.APPLICATION_JSON)
//                    jsonPath("$.sharedIds[0]") {value("")}
//                }
//            }
//        mockMvc.get("/api/user/username/friendUser")
//            .andDo{ print() }
//            .andExpect {
//                status { isOk() }
//                content {
//                    contentType(MediaType.APPLICATION_JSON)
//                    jsonPath("$.fileIdShared[0]") {value("")}
//                }
//            }
    }
    @Test
    @Ignore
    fun `Delete existing file`() {
        val fileId = id

        mockMvc.delete("$BASE_URL/$fileId")
            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$BASE_URL/$fileId")
            .andDo{ print() }
            .andExpect {
                status { isNotFound() }
            }
    }
}