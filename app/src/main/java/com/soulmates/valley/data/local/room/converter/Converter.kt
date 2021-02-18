package com.soulmates.valley.data.local.room.converter

import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun getIntListFromString(interest: String): List<Int> {
        val list: MutableList<Int> = ArrayList()
        val array = interest.split(",".toRegex()).toTypedArray()
        for (s in array) {
            if (s.isNotEmpty()) {
                list.add(s.toInt())
            }
        }
        return list
    }

    @TypeConverter
    fun getStringFromIntList(list: List<Int>): String {
        var interestIdx = ""
        for (i in list) {
            interestIdx += ",$i"
        }
        return interestIdx
    }

    @TypeConverter
    fun getStringListFromString(string: String): List<String> {
        val list: MutableList<String> = ArrayList()
        val array = string.split(",".toRegex()).toTypedArray()
        for (s in array) {
            if (s.isNotEmpty()) {
                list.add(s)
            }
        }
        return list
    }

    @TypeConverter
    fun getStringFromStringList(list: List<String>): String {
        var string = ""
        for (i in list) {
            string += ",$i"
        }
        return string
    }
}