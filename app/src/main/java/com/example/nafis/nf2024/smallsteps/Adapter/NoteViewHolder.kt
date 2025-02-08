package com.example.nafis.nf2024.smallsteps.Adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteItemClick
import com.example.nafis.nf2024.smallsteps.Model.Notes
import com.example.nafis.nf2024.smallsteps.databinding.NoteLayoutBinding
import java.util.Random

class NoteViewHolder(var binding: NoteLayoutBinding, var callback: NoteItemClick):RecyclerView.ViewHolder(binding.root) {

    fun bind(note: Notes){
        binding.tvNoteTitle.text = note.noteTitle
       binding.tvNoteBody.text = note.noteBody

        val random = Random()
        val color = Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )

        binding.ibColor.setBackgroundColor(color)

        itemView.setOnClickListener {
            callback.onNoteClick(note)
        }
        itemView.setOnLongClickListener {
            callback.onLongClick(note)
            true
        }
    }
}
