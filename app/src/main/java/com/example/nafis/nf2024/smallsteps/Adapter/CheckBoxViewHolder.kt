package com.example.nafis.nf2024.smallsteps.Adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.smallsteps.DiffUtil.CheckBoxItemClick
import com.example.nafis.nf2024.smallsteps.Model.checkbox
import com.example.nafis.nf2024.smallsteps.databinding.CheckboxitemBinding

class CheckBoxViewHolder(
    var binding: CheckboxitemBinding,
    private val adapter: CheckBoxAdapter,
    private val s: Boolean,
    private var callback: CheckBoxItemClick
):RecyclerView.ViewHolder(binding.root) {

    fun bind(note: checkbox, pos: Int) {
        initializeCheckboxState(note,pos)
        initializeEditTextState(note)

        if (s) {
            disableEditing()
        } else {
            enableEditing()
        }

        setupListeners(note, pos)
    }

    private fun initializeCheckboxState(note: checkbox, pos: Int) {
        binding.checkboxitem.isChecked = note.isChecked
        if (note.isChecked) complete(note,pos) else reset(note,pos)
    }

    private fun initializeEditTextState(note: checkbox) {
        binding.checkboxedittext.setText(note.text)
    }

    private fun disableEditing() {
        with(binding) {
            checkboxedittext.apply {
                isFocusable = false
                isClickable = false
                isFocusableInTouchMode = false
                isEnabled = false
            }
            checkboxitem.isEnabled = false
            deleteIcon.visibility = View.GONE
        }
    }

    private fun enableEditing() {
        with(binding) {
            checkboxedittext.apply {
                isFocusable = true
                isClickable = true
                isFocusableInTouchMode = true
                isEnabled = true
            }
            checkboxitem.isEnabled = true
            deleteIcon.visibility = View.VISIBLE
        }
    }

    private fun setupListeners(note: checkbox, pos: Int) {
        binding.checkboxitem.setOnCheckedChangeListener { _, isChecked ->
            note.isChecked = isChecked
            if (isChecked) complete(note, pos) else reset(note, pos)
        }

        binding.deleteIcon.setOnClickListener {
            deleteCheckbox(pos,note)
        }

        binding.checkboxedittext.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                note.text = binding.checkboxedittext.text.toString()
                adapter.updateItem(pos, note)
            }
        }
    }

    private fun complete(note: checkbox, pos: Int) {
        binding.checkboxedittext.apply {
            setTextColor(Color.GRAY)
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            isFocusable = false
            isClickable = false
            isFocusableInTouchMode = false
            isEnabled = false
        }
        adapter.updateItem(pos, note) // ✅ Ensure UI updates correctly
    }

    private fun reset(note: checkbox, pos: Int) {
        binding.checkboxedittext.apply {
            setTextColor(Color.BLACK)
            paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            isFocusable = true
            isClickable = true
            isFocusableInTouchMode = true
            isEnabled = true
        }
        adapter.updateItem(pos, note) // ✅ Ensure UI updates correctly
    }

    private fun deleteCheckbox(pos: Int, note: checkbox) {
        AlertDialog.Builder(binding.root.context).apply {
            setTitle("Delete Note")
            setMessage("You want to delete this Note?")
            setPositiveButton("Delete") { _, _ ->
                callback.onDeleteClick(pos,note)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }
}