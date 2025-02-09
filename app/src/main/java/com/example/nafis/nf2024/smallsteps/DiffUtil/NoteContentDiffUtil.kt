package com.example.nafis.nf2024.smallsteps.DiffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.smallsteps.Model.NoteContent

class NoteContentDiffUtil:DiffUtil.ItemCallback<NoteContent>() {
    override fun areItemsTheSame(oldItem: NoteContent, newItem: NoteContent): Boolean {
         return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteContent, newItem: NoteContent): Boolean {
         return oldItem.Text==newItem.Text &&
                 oldItem.imgUri==newItem.imgUri
    }
}