package com.example.nafis.nf2024.smallsteps.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.Repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(
    app: Application,
    private val noteRepository: NoteRepository
) :
    AndroidViewModel(app)

{
     fun addNote(note: Notes) =
         viewModelScope.launch {
             noteRepository.insertNote(note)
         }

    fun deleteNote(note: Notes){
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun updateNote(note: Notes){
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    fun getAllNotes() = noteRepository.getAllNotes()

    fun searchNote(query : String?) =
        noteRepository.searchNote(query)




}