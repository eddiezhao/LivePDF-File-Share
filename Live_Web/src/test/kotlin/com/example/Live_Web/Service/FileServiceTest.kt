package com.example.Live_Web.Service

import com.example.Live_Web.DataSource.FileDataSource
import org.junit.jupiter.api.Assertions.*
import com.example.Live_Web.Service.FileService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test


internal class FileServiceTest {

    private val dataSource: FileDataSource = mockk(relaxed = true) // Returns basic values (0, "", emptyList)
    private val fileService: FileService = FileService(dataSource)

    @Test
    fun `Calling file data source` () {

        val files = fileService.getFiles()

        verify(exactly = 1) { dataSource.getFiles() }
    }

}