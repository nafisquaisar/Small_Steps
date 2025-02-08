package com.example.nafis.nf2024.smallsteps.DiffUtil

import androidx.recyclerview.widget.DiffUtil
import com.example.nafis.nf2024.smallsteps.Model.checkbox

class CheckBoxDiffUtil:DiffUtil.ItemCallback<checkbox>() {
    override fun areItemsTheSame(oldItem: checkbox, newItem: checkbox): Boolean {
         return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: checkbox, newItem: checkbox): Boolean {
         return oldItem.isChecked==newItem.isChecked &&
                 oldItem.text== newItem.text
    }
}