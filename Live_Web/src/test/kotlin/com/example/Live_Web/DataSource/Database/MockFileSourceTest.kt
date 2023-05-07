package com.example.Live_Web.DataSource.Database

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import com.example.Live_Web.DatabaseRepositories.FileRepository
import com.example.Live_Web.DatabaseRepositories.UserRepository


internal class MockFileSourceTest (
    private val fileRepository: FileRepository,
    private val userRepository: UserRepository
        ) {
    private val mockFileSource = MockFileSource(fileRepository, userRepository);
    @Test
    fun `Should provide a collection of files` () {
        val files = mockFileSource.getFiles();

        assertThat(files).isNotEmpty;
    }

    @Test
    fun `Mock Data has information` () {
        val files = mockFileSource.getFiles();

        assertThat(files).allMatch{it.name.isNotEmpty()}
        assertThat(files).allMatch{it.id.isNotEmpty()}
    }
}