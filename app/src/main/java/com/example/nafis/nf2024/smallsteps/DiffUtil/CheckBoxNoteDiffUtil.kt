package com.example.nafis.nf2024.smallsteps.DiffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote

class CheckBoxNoteDiffUtil:DiffUtil.ItemCallback<CheckBoxNote>() {
    override fun areItemsTheSame(oldItem: CheckBoxNote, newItem: CheckBoxNote): Boolean {
         return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: CheckBoxNote, newItem: CheckBoxNote): Boolean {
         return oldItem.title==newItem.title &&
                 oldItem.checkBoxes.containsAll(newItem.checkBoxes) &&
                 newItem.checkBoxes.containsAll(oldItem.checkBoxes)
    }
}