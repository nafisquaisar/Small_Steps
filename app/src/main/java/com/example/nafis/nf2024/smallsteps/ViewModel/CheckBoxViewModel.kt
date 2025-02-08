package com.example.nafis.nf2024.smallsteps.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Repository.CheckBoxRepository
import kotlinx.coroutines.launch

class CheckBoxViewModel(
    app: Application,
    private val checkBoxRepository: CheckBoxRepository
) : AndroidViewModel(app) {

    fun addCheckBoxNote(note: CheckBoxNote) =
        viewModelScope.launch {
            checkBoxRepository.insertCheckBox(note)
        }

    fun deleteCheckBoxNote(note: CheckBoxNote) =
        viewModelScope.launch {
            checkBoxRepository.deleteCheckBox(note)
        }

    fun updateCheckBoxNote(note: CheckBoxNote) =
        viewModelScope.launch {
            checkBoxRepository.updateCheckBox(note)
        }

    fun getAllCheckBoxNotes() = checkBoxRepository.getAllCheckBoxNotes()
}
