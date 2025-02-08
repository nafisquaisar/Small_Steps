package com.example.nafis.nf2024.smallsteps.DiffUtil

import android.widget.CheckBox
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.Model.checkbox

interface CheckBoxItemClick {
    fun onCheckClick(note: checkbox)
}
