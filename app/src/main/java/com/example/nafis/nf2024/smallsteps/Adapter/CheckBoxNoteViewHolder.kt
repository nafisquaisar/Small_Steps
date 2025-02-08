package com.example.nafis.nf2024.smallsteps.Adapter

import android.graphics.Color
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxNoteItemClick
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.databinding.CheckNoteLayoutBinding
import java.util.Random

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
        val random = Random()
        val colorib = Color.argb(
            255,
            random.nextInt(256),
            random.nextInt(256),
            random.nextInt(256)
        )

        binding.ibColor.setBackgroundColor(colorib)

        binding.bgmain.setBackgroundColor(color)
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        checkBoxAdapter = CheckBoxAdapter(true)
        binding.recyclerView.adapter = checkBoxAdapter



        binding.root.isClickable = true
        binding.root.isFocusable = true
        binding.recyclerView.isFocusable = false
        binding.recyclerView.isFocusableInTouchMode = false

        checkBoxAdapter.submitList(note.checkBoxes)
        // Handle single click and long click using the overlay
        binding.clickOverlay.setOnClickListener {
            callback.onCheckNoteClick(note)
        }

        binding.clickOverlay.setOnLongClickListener {
            callback.onLongClick(note)
            true
        }

    }
}