package com.example.nafis.nf2024.smallsteps.DiffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.smallsteps.Model.Notes

class NoteDiffUtil:DiffUtil.ItemCallback<Notes>() {
    override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
         return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
         return oldItem.noteTitle==newItem.noteTitle &&
                 oldItem.noteBody==newItem.noteBody
    }
}