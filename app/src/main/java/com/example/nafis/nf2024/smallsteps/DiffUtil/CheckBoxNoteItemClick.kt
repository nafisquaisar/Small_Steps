package com.example.nafis.nf2024.smallsteps.DiffUtil

import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Model.Notes

interface CheckBoxNoteItemClick {
    fun onCheckNoteClick(note: CheckBoxNote)
    fun onLongClick(note: CheckBoxNote)
}
