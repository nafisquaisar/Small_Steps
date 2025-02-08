package com.example.nafis.nf2024.smallsteps.Model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CheckBoxConverter {
    @TypeConverter
    fun fromcheckboxList(list: ArrayList<checkbox>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun tocheckboxList(json: String): ArrayList<checkbox> {
        val type = object : TypeToken<ArrayList<checkbox>>() {}.type
        return Gson().fromJson(json, type)
    }
}
