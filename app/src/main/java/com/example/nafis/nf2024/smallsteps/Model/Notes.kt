package com.example.nafis.nf2024.smallsteps.Model

import com.example.nafis.nf2024.smallsteps.NoteContentConverters
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Notes(

    @PrimaryKey(autoGenerate = true)
    @TypeConverters(NoteContentConverters::class)
    val id:Int,
    val noteTitle:String,
    val noteBody:String,
    var bgColor:String,
    val createdDate:String,
    val contentList:ArrayList<NoteContent> = arrayListOf()
) : Parcelable
