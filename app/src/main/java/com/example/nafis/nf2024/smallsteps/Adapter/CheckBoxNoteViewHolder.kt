package com.example.nafis.nf2024.smallsteps.Adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxNoteItemClick
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.databinding.CheckNoteLayoutBinding

class CheckBoxNoteViewHolder(
    var binding: CheckNoteLayoutBinding,
    var callback: CheckBoxNoteItemClick
):RecyclerView.ViewHolder(binding.root) {

    private lateinit var checkBoxAdapter: CheckBoxAdapter

    fun bind(note: CheckBoxNote) {
        binding.tvNoteTitle.text = note.title
        val color = try {
            android.graphics.Color.parseColor(note.backgroundColor)
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.parseColor("#FFFFFF")  // Default to white
        }
        binding.bgmain.setBackgroundColor(color)
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        checkBoxAdapter = CheckBoxAdapter(true)
        binding.recyclerView.adapter = checkBoxAdapter

        checkBoxAdapter.submitList(note.checkBoxes)

        itemView.setOnClickListener {
            callback.onCheckNoteClick(note)
        }

        itemView.setOnLongClickListener {
            callback.onLongClick(note)
            true
        }
    }
}