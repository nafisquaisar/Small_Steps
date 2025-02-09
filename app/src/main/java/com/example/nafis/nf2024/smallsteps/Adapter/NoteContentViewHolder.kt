package com.example.nafis.nf2024.smallsteps.Adapter

import android.app.AlertDialog
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteContentClick
import com.example.nafis.nf2024.smallsteps.Model.NoteContent
import com.example.nafis.nf2024.smallsteps.databinding.NoteContentLayoutBinding

class NoteContentViewHolder(var binding: NoteContentLayoutBinding, var callback: NoteContentClick):RecyclerView.ViewHolder(binding.root) {

    fun bind(note: NoteContent) {
        if (!note.isText) {
            binding.noteImage.visibility = View.VISIBLE
            binding.noteEditText.visibility = View.GONE

            Glide.with(binding.root.context)
                .load(Uri.parse(note.imgUri))
                .into(binding.noteImage)
        } else {
            binding.noteImage.visibility = View.GONE
            binding.noteEditText.visibility = View.VISIBLE
            binding.noteEditText.setText(note.Text)
        }

        binding.deletebtn.setOnClickListener {
            deleteItem(adapterPosition, note) // Use adapterPosition instead of position
        }
    }

    private fun deleteItem(position: Int, note: NoteContent) {
            AlertDialog.Builder(binding.root.context).apply {
                setTitle("Delete Note")
                setMessage("You want to delete this Note ?")
                setPositiveButton("Delete"){_,_,->
                    callback.onDelete(pos = position,note)
                }
                setNegativeButton("Cancel",null)
            }.create().show()
    }
}
