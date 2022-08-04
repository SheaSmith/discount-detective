package com.example.cosc345.scraperapp.typeconverters

import androidx.room.TypeConverter

class RoomStringListConverter {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { value.joinToString { "," } }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { value.split(",") }
    }
}