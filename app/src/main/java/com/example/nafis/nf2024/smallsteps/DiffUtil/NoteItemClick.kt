package com.example.nafis.nf2024.smallsteps.DiffUtil

import com.example.nafis.nf2024.smallsteps.Model.Notes

interface NoteItemClick {
    fun onNoteClick(note: Notes)
    fun onLongClick(note: Notes)
}
