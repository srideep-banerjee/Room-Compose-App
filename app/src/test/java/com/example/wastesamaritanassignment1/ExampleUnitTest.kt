package com.example.wastesamaritanassignment1

import com.example.wastesamaritanassignment1.model.ListStringConverter
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun ListStringConverter_fromStringList() {
        val res = ListStringConverter().fromStringList(listOf("1,2","3\\4","5"))
        assertEquals("1\\,2,3\\\\4,5", res)
    }

    @Test
    fun ListStringConverter_toStringList() {
        val res = ListStringConverter().toStringList("1\\,2,3\\\\4,5")
        assertEquals(listOf("1,2","3\\4","5"), res)
    }
}