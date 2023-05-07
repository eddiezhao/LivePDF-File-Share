package com.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class APIClientTest {
    private val apiClient = APIClient()

    @Test
    fun `isSuccess should return true for codes between 200 and 299`() {
        assertTrue(apiClient.isSuccess(200))
        assertTrue(apiClient.isSuccess(250))
        assertTrue(apiClient.isSuccess(299))
    }

    @Test
    fun `isSuccess should return false for codes not between 200 and 299`() {
        assertFalse(apiClient.isSuccess(199))
        assertFalse(apiClient.isSuccess(300))
        assertFalse(apiClient.isSuccess(404))
    }
}