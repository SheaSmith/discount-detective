package com.example.cosc345.scraperapp.typeconverters

import androidx.room.TypeConverter

/**
 * Room convertors for converting specific objects into SQL objects.
 */
class RoomStringListConverter {
    /**
     * Convert a list of strings into a string that can be saved into the database.
     *
     * @param value The list to convert.
     * @return The contents of the list, delimited by a comma.
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { value.joinToString(",") }
    }

    /**
     * Convert a string value from the database into a list of strings.
     *
     * @param value The comma delimited string to convert.
     * @return A list of strings based on the input string.
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { value.split(",") }
    }
}