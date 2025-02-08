package com.example.nafis.nf2024.smallsteps.ViewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nafis.nf2024.smallsteps.Repository.CheckBoxRepository

class CheckBoxNoteViewModelFactory(
    private val app: Application,
    private val checkBoxNoteRepository: CheckBoxRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CheckBoxViewModel(app, checkBoxNoteRepository) as T
    }

}
