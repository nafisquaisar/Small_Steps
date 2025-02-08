package com.example.nafis.nf2024.smallsteps.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteContent(
    val id:Int=0,
    val Text:String="",
    val imgUri:String=""
)  : Parcelable
