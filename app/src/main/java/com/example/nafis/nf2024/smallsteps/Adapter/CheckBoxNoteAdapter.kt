package com.example.nafis.nf2024.smallsteps.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxNoteDiffUtil
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxNoteItemClick
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.databinding.CheckNoteLayoutBinding

class CheckBoxNoteAdapter(val list:ArrayList<CheckBoxNote>, var callback:CheckBoxNoteItemClick) : ListAdapter<CheckBoxNote, CheckBoxNoteViewHolder>(CheckBoxNoteDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBoxNoteViewHolder {
         return CheckBoxNoteViewHolder(
            CheckNoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false),
             callback
         )
    }

    override fun onBindViewHolder(holder: CheckBoxNoteViewHolder, position: Int) {
         var model= getItem(position)
         holder.bind(model)
    }


}