package com.example.nafis.nf2024.smallsteps

import androidx.room.TypeConverter
import com.example.nafis.nf2024.smallsteps.Model.NoteContent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteContentConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromNoteContentList(contentList: ArrayList<NoteContent>?): String {
        return gson.toJson(contentList)
    }

    @TypeConverter
    fun toNoteContentList(contentListJson: String?): ArrayList<NoteContent> {
        val listType = object : TypeToken<ArrayList<NoteContent>>() {}.type
        return gson.fromJson(contentListJson, listType) ?: arrayListOf()
    }
}