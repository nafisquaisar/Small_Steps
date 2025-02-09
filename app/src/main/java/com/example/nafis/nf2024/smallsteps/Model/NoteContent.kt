package com.example.nafis.nf2024.smallsteps.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteContent(
    val id:Int=0,
    var Text:String="",
    val imgUri:String="",
    val isText:Boolean=false
)  : Parcelable
