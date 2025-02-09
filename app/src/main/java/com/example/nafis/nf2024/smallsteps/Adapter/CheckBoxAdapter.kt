package com.example.nafis.nf2024.smallsteps.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxDiffUtil
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxItemClick
import com.example.nafis.nf2024.smallsteps.Model.checkbox
import com.example.nafis.nf2024.smallsteps.databinding.CheckboxitemBinding

class CheckBoxAdapter(private var callback:CheckBoxItemClick,private var s: Boolean =false) : ListAdapter<checkbox, CheckBoxViewHolder>(
    CheckBoxDiffUtil()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBoxViewHolder {
         return CheckBoxViewHolder(
            CheckboxitemBinding.inflate(LayoutInflater.from(parent.context),parent,false),
             this
             ,s,callback
         )
    }

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
         var model= getItem(position)
         holder.bind(model,position)
    }

    fun updateItem(position: Int, updatedNote: checkbox) {
        if (position in currentList.indices) {
            val newList = currentList.toMutableList()
            newList[position] = updatedNote.copy() // ✅ Ensure ListAdapter detects change
            submitList(newList.toList()) // ✅ Properly update the list
        }
    }

    fun deleteItem(position: Int) {
        if (position in currentList.indices) {
            val newList = currentList.toMutableList()
            newList.removeAt(position)
            submitList(newList.toList())
        }
    }





}