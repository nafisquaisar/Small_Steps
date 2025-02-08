package com.example.nafis.nf2024.smallsteps.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteDiffUtil
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteItemClick
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.databinding.NoteLayoutBinding

class NoteAdapter(val list:ArrayList<Notes>, var callback:NoteItemClick) : ListAdapter<Notes, NoteViewHolder>(NoteDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
         return NoteViewHolder(
            NoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false),
             callback
         )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
         var model= getItem(position)
         holder.bind(model)
    }
}