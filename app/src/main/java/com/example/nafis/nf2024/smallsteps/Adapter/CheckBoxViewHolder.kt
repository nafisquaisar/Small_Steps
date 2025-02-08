package com.example.nafis.nf2024.smallsteps.Adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.smallsteps.Model.checkbox
import com.example.nafis.nf2024.smallsteps.databinding.CheckboxitemBinding

class CheckBoxViewHolder(
    var binding: CheckboxitemBinding,
    private val adapter: CheckBoxAdapter,
    private val s: Boolean
):RecyclerView.ViewHolder(binding.root) {

    fun bind(note: checkbox, pos: Int) {
        initializeCheckboxState(note)
        initializeEditTextState(note)

        if (s) {
            disableEditing()
        } else {
            enableEditing()
        }

        setupListeners(note, pos)
    }

    private fun initializeCheckboxState(note: checkbox) {
        binding.checkboxitem.isChecked = note.isChecked
        if (note.isChecked) complete() else reset()
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
            if (isChecked) complete() else reset()
        }

        binding.deleteIcon.setOnClickListener {
            deleteCheckbox(pos)
        }

        binding.checkboxedittext.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                note.text = binding.checkboxedittext.text.toString()
                adapter.updateItem(pos, note)
            }
        }
    }

    private fun complete() {
        binding.checkboxedittext.apply {
            setTextColor(Color.GRAY)
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            isFocusable = false
            isClickable = false
            isFocusableInTouchMode = false
            isEnabled = false
        }
    }

    private fun reset() {
        binding.checkboxedittext.apply {
            setTextColor(Color.BLACK)
            paintFlags = paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            isFocusable = true
            isClickable = true
            isFocusableInTouchMode = true
            isEnabled = true
        }
    }

    private fun deleteCheckbox(pos: Int) {
        AlertDialog.Builder(binding.root.context).apply {
            setTitle("Delete Note")
            setMessage("You want to delete this Note?")
            setPositiveButton("Delete") { _, _ ->
                adapter.deleteItem(pos)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }
}