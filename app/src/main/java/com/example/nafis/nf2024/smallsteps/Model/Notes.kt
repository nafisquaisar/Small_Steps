package com.example.nafis.nf2024.smallsteps.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Notes(

    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val noteTitle:String,
    val noteBody:String
) : Parcelable
