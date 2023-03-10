package com.pi.recipeapp

import org.junit.Test

import org.junit.Assert.*
import java.util.UUID

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val testString = "ABCDE test"
        val uuid1 = UUID.fromString(testString)
        val uuid2 = UUID.fromString(testString)
        assertEquals(uuid1, uuid2)
    }
}