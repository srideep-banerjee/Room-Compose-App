package com.example.wastesamaritanassignment1.model

import androidx.room.TypeConverter

class ListStringConverter {

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val stringBuilder = StringBuilder()

        for (i in value.indices) {
            val str = value[i]
            for (j in str.indices) {
                if (str[j] == '\\') {
                    stringBuilder.append("\\\\")
                } else if (str[j] == ',') {
                    stringBuilder.append("\\,")
                } else {
                    stringBuilder.append(str[j])
                }
            }

            if(i != value.size - 1)stringBuilder.append(',')
        }

        return stringBuilder.toString()
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val list = mutableListOf<String>()

        val stringBuilder = StringBuilder()
        var ind = 0
        while (ind < value.length) {
            if(value[ind] == '\\') {
                ind++
                if (value[ind] == '\\') {
                    stringBuilder.append('\\')
                } else if (value[ind] == ',') {
                    stringBuilder.append(',')
                }
            } else if (value[ind] == ',') {
                list.add(stringBuilder.toString())
                stringBuilder.clear()
            } else {
                stringBuilder.append(value[ind])
            }
            ind++
        }
        list.add(stringBuilder.toString())

        return list
    }
}