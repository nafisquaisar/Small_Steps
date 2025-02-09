package com.example.nafis.nf2024.smallsteps.Adapter

import android.R
import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nafis.nf2024.smallsteps.DiffUtil.NoteContentClick
import com.example.nafis.nf2024.smallsteps.Model.NoteContent
import com.example.nafis.nf2024.smallsteps.R.*
import com.example.nafis.nf2024.smallsteps.databinding.NoteContentLayoutBinding
import com.github.chrisbanes.photoview.PhotoView

class NoteContentViewHolder(var binding: NoteContentLayoutBinding, var callback: NoteContentClick):RecyclerView.ViewHolder(binding.root) {

    fun bind(note: NoteContent) {
        if (!note.isText) {
            binding.noteImage.visibility = View.VISIBLE
            binding.noteEditText.visibility = View.GONE

            Glide.with(binding.root.context)
                .load(Uri.parse(note.imgUri))
                .into(binding.noteImage)

            binding.noteImage.setOnClickListener {
                showFullScreenImageDialog(note.imgUri)
            }
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

    private fun showFullScreenImageDialog(imageUrl: String) {
        val dialog = Dialog(binding.root.context)
        dialog.setContentView(layout.image_view)
        val photoView: PhotoView = dialog.findViewById(id.photoView)

        Glide.with(binding.root.context)
            .load(Uri.parse(imageUrl)) // Load with Uri.parse
            .into(photoView)

        dialog.show()

        val window = dialog.window
        val params = window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        window?.attributes = params


        photoView.setOnClickListener {
            dialog.dismiss()
        }

    }
}
