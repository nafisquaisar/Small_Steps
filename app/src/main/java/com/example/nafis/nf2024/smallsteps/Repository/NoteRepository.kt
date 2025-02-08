package com.example.nafis.nf2024.smallsteps.Repository

import com.example.nafis.nf2024.smallsteps.Database.NoteDatabase
import com.example.nafis.nf2024.smallsteps.Model.Notes


class NoteRepository(private val db: NoteDatabase) {

    suspend fun insertNote(note : Notes) = db.getNoteDao().insertNote(note)
    suspend fun deleteNote(note : Notes) = db.getNoteDao().deleteNote(note)
    suspend fun updateNote(note : Notes) = db.getNoteDao().updateNote(note)

    fun getAllNotes() = db.getNoteDao().getAllNote()
    fun searchNote(query: String?) = query?.let {
        val searchQuery = "%$it%"
        db.getNoteDao().searchNote(searchQuery)
    }


}