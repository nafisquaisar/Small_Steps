package com.example.nafis.nf2024.smallsteps.DiffUtil

import com.example.nafis.nf2024.smallsteps.Model.NoteContent

interface NoteContentClick {
    fun onNoteContentClick(note: NoteContent)
    fun onDelete(pos:Int,note: NoteContent)
    fun onTextAdd(pos:Int,note: NoteContent)
}
