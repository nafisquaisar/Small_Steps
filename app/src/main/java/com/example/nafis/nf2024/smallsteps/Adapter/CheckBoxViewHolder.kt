package com.example.nafis.nf2024.smallsteps.Adapter

import android.graphics.Color
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.example.nafis.nf2024.smallsteps.Model.checkbox
import com.example.nafis.nf2024.smallsteps.ViewModel.CheckBoxViewModel
import com.example.nafis.nf2024.smallsteps.databinding.CheckboxitemBinding

class CheckBoxViewHolder(
    var binding: CheckboxitemBinding,
    private val adapter: CheckBoxAdapter,
    private val s: Boolean
):RecyclerView.ViewHolder(binding.root) {

    fun bind(note: checkbox, pos: Int) {
        binding.checkboxitem.isChecked = note.isChecked
        binding.checkboxedittext.setText(note.text)

        if (s) {
            binding.checkboxedittext.isFocusable = false
            binding.checkboxedittext.isClickable = false  // Prevent clicks
            binding.checkboxedittext.isFocusableInTouchMode = false  // Ensure it doesn't get focus on touch

            binding.checkboxitem.isFocusable = false
            binding.checkboxitem.isClickable = false  // Prevent clicks
            binding.checkboxitem.isFocusableInTouchMode = false  // Ensure it doesn't get focus on touch
        }


        if (note.isChecked) {
            binding.checkboxedittext.setTextColor(Color.GRAY)  // Change text color to gray
            binding.checkboxedittext.paintFlags =
                binding.checkboxedittext.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG  // Cross the text
            binding.checkboxedittext.isFocusable = false // Make EditText non-editable when checked
            binding.checkboxedittext.isFocusableInTouchMode = false
        } else {
            binding.checkboxedittext.setTextColor(Color.BLACK)  // Set text color to black
            binding.checkboxedittext.paintFlags =
                binding.checkboxedittext.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()  // Remove the strike-through
            binding.checkboxedittext.isFocusable = true // Make EditText editable when unchecked
            binding.checkboxedittext.isFocusableInTouchMode =
                true
        }

        binding.checkboxitem.setOnCheckedChangeListener { _, isChecked ->
            note.isChecked = isChecked

            if (isChecked) {
                complete()  // Strike through and gray text
            } else {
                reset()  // Reset to normal text
            }

        }

            // Handle text change when focus is lost
            binding.checkboxedittext.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    note.text = binding.checkboxedittext.text.toString()
                    adapter.updateItem(pos, note)
                }
            }



    }

    // Mark the item as completed (cross the text and gray color)
    private fun complete() {
        binding.checkboxedittext.setTextColor(Color.GRAY)
        binding.checkboxedittext.paintFlags =
            binding.checkboxedittext.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    // Reset the item to normal state (no cross and black color)
    private fun reset() {
        binding.checkboxedittext.setTextColor(Color.BLACK)
        binding.checkboxedittext.paintFlags =
            binding.checkboxedittext.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }


}
