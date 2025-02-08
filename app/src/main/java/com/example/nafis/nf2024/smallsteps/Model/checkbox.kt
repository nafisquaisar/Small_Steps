package com.example.nafis.nf2024.smallsteps.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class checkbox(
    var id:Int=0,
    var isChecked: Boolean = false,
    var text: String = "New Checkbox"
) : Parcelable
