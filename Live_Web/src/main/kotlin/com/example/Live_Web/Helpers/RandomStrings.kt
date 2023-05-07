package com.example.Live_Web.Helpers

/**
 * Random String Class.
 */
class RandomStrings {

    /**
     * Creates random string of length 'length'.
     */
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}