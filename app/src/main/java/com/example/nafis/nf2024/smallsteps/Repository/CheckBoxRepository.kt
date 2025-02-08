package com.example.nafis.nf2024.smallsteps.Repository

import com.example.nafis.nf2024.smallsteps.Database.NoteDatabase
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote

class CheckBoxRepository(private val db: NoteDatabase) {

    suspend fun insertCheckBox(note: CheckBoxNote) = db.getCheckBoxDao().insertCheckBoxNote(note)
    suspend fun deleteCheckBox(note: CheckBoxNote) = db.getCheckBoxDao().deleteCheckBoxNote(note)
    suspend fun updateCheckBox(note: CheckBoxNote) = db.getCheckBoxDao().updateCheckBoxNote(note)

    fun getAllCheckBoxNotes() = db.getCheckBoxDao().getAllCheckBoxNotes()

    fun searchCheckBoxNote(query: String?) = query?.let {
        val searchQuery = "%$it%"
        db.getCheckBoxDao().searchCheckBoxNote(searchQuery)
    }
}
