package com.services

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*


/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server.
 * @author www.codejava.net
 */
class MultiPartBodyPublisher(requestURL: String?, private val charset: String) {
    private val boundary: String = "---" + System.currentTimeMillis() + "---"
    private val httpConn: HttpURLConnection
    private val outputStream: OutputStream
    private val writer: PrintWriter

    init {
        val url = URL(requestURL)
        httpConn = url.openConnection() as HttpURLConnection
        httpConn.useCaches = false
        httpConn.doOutput = true // indicates POST method
        httpConn.doInput = true
        httpConn.setRequestProperty(
            "Content-Type",
            "multipart/form-data; boundary=$boundary"
        )
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent")
        httpConn.setRequestProperty("Test", "Bonjour")
        outputStream = httpConn.outputStream
        writer = PrintWriter(
            OutputStreamWriter(outputStream, charset),
            true
        )
    }


    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..."></input>
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    @Throws(IOException::class)
    fun addFilePart(fieldName: String, uploadFile: File) {
        val fileName = uploadFile.name
        writer.append("--$boundary").append(LINE_FEED)
        writer.append(
            "Content-Disposition: form-data; name=\"" + fieldName
                    + "\"; filename=\"" + fileName + "\""
        )
            .append(LINE_FEED)
        writer.append(
            (
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
        )
            .append(LINE_FEED)
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED)
        writer.append(LINE_FEED)
        writer.flush()
        val inputStream = FileInputStream(uploadFile)
        val buffer = ByteArray(4096)
        var bytesRead = -1
        while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.flush()
        inputStream.close()
        writer.append(LINE_FEED)
        writer.flush()
    }

    @Throws(IOException::class)
    fun finish(): Number {
        val response: MutableList<String?> = ArrayList()
        writer.append(LINE_FEED).flush()
        writer.append("--$boundary--").append(LINE_FEED)
        writer.close()

        // checks server's status code first
        val status = httpConn.responseCode
        if (status == HttpURLConnection.HTTP_CREATED) {
            val reader = BufferedReader(
                InputStreamReader(
                    httpConn.inputStream
                )
            )
            var line: String? = null
            while ((reader.readLine().also { line = it }) != null) {
                response.add(line)
            }
            reader.close()
            httpConn.disconnect()
        } else {
            throw IOException("Server returned non-OK status: $status")
        }
        return httpConn.responseCode
    }

    companion object {
        private const val LINE_FEED = "\r\n"
    }
}