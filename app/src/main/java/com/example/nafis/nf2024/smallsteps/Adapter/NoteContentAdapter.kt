package com.example.nafis.nf2024.smallsteps.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteContentClick
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteContentDiffUtil
import com.example.nafis.nf2024.smallsteps.Model.NoteContent
import com.example.nafis.nf2024.smallsteps.databinding.NoteContentLayoutBinding

class NoteContentAdapter(private var callback:NoteContentClick) : ListAdapter<NoteContent, NoteContentViewHolder>(
    NoteContentDiffUtil()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteContentViewHolder {
         return NoteContentViewHolder(
            NoteContentLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false),callback)
    }

    override fun onBindViewHolder(holder: NoteContentViewHolder, position: Int) {
         var model= getItem(position)
         holder.bind(model)
    }
}