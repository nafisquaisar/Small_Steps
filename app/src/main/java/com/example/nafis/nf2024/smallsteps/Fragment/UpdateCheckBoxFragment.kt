package com.example.nafis.nf2024.smallsteps.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxAdapter
import com.example.nafis.nf2024.smallsteps.Adapter.CheckBoxViewHolder
import com.example.nafis.nf2024.smallsteps.MainActivity
import com.example.nafis.nf2024.smallsteps.Model.CheckBoxNote
import com.example.nafis.nf2024.smallsteps.Model.checkbox
import com.example.nafis.nf2024.smallsteps.R
import com.example.nafis.nf2024.smallsteps.ViewModel.CheckBoxViewModel
import com.example.nafis.nf2024.smallsteps.databinding.FragmentUpdateCheckBoxBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.properties.Delegates


class UpdateCheckBoxFragment : Fragment() {
   private lateinit var binding:FragmentUpdateCheckBoxBinding
    private lateinit var currentNote : CheckBoxNote
    private val arg :UpdateCheckBoxFragmentArgs by navArgs()
    private lateinit var  checkBoxViewModel:CheckBoxViewModel
    private lateinit var list:ArrayList<checkbox>
    private var color by Delegates.notNull<Int>()
    private lateinit var checkBoxAdapter: CheckBoxAdapter
    private lateinit var checkboxContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentUpdateCheckBoxBinding.inflate(inflater,container,false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Update CheckNote"
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_ios_24)?.apply {
                setTint(requireContext().getColor(R.color.white))
            }
        )

        currentNote=arg.checknote!!
        checkBoxViewModel= (activity as MainActivity).checkNoteViewModel

       setAllLayout()


        binding.addcheckbox.setOnClickListener {
            addNewCheckbox()
        }
        binding.backgroundcolorchange.setOnClickListener {
            showBottomDialog()
        }

        binding.fabDoneCheckbox.setOnClickListener {
            updateAllCheckboxText()
            updateCheckBox()
        }
        return binding.root
    }

    private fun updateAllCheckboxText() {
        for (i in 0 until binding.checkboxRecyclerView.childCount) {
            val holder = binding.checkboxRecyclerView.findViewHolderForAdapterPosition(i)
            if (holder is CheckBoxViewHolder) {
                list[i].text = holder.binding.checkboxedittext.text.toString()
            }

        }
    }
    private fun setAllLayout() {
        binding.etNoteTitle.setText(currentNote.title)
        binding.date.setText(currentNote.dateCreated)

        try {
            color = android.graphics.Color.parseColor(currentNote.backgroundColor)
            binding.mainbg.setBackgroundColor(color)  // Use setBackgroundColor instead of setBackgroundResource
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            color = android.graphics.Color.WHITE  // Fallback to white if parsing fails
            binding.mainbg.setBackgroundColor(color)
        }

        checkboxContainer = binding.checkboxContainer

        list = ArrayList()
        list.clear()
        list=currentNote.checkBoxes
        binding.checkboxRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.checkboxRecyclerView.isNestedScrollingEnabled = false
        checkBoxAdapter = CheckBoxAdapter()
        binding.checkboxRecyclerView.adapter = checkBoxAdapter
        checkBoxAdapter.submitList(currentNote.checkBoxes)
    }


    //================   update the checkbox note    =======================
    private fun updateCheckBox() {
        val currentDate = currentNote.dateCreated
        val title = binding.etNoteTitle.text.toString().trim()

        // Convert HEX color safely
        color = try {
            android.graphics.Color.parseColor(currentNote.backgroundColor)
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.WHITE // Default to white if parsing fails
        }

        if (title.isNotEmpty()) {
            val note = CheckBoxNote(
                id = currentNote.id, // Preserve existing ID
                title = title,
                checkBoxes = list,
                backgroundColor = String.format("#%06X", 0xFFFFFF and color), // Convert color back to HEX string
                dateCreated = currentDate
            )

            checkBoxViewModel.updateCheckBoxNote(note)

            Toast.makeText(
                context,
                "Note Updated Successfully",
                Toast.LENGTH_LONG
            ).show()

            val bundle = Bundle().apply {
                putInt("selectedTabIndex", 1) // 1 for "CheckList" tab
            }
            requireParentFragment().findNavController()
                .navigate(R.id.action_updateCheckBoxFragment_to_noteTypeFragment,bundle)
        } else {
            Toast.makeText(
                context,
                "Please enter note Title",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    //================update the checkbox note=======================


    //===============add check box =============================
    private fun addNewCheckbox() {
        val newCheckbox = checkbox(id = list.size + 1, isChecked = false, text = "New Checkbox")
        Toast.makeText(requireContext(),"Before Add: ${list.size}", Toast.LENGTH_SHORT).show()
        list.add(newCheckbox) // Add new checkbox to the list
        Toast.makeText(requireContext(),"After Add: ${list.size}", Toast.LENGTH_SHORT).show()

        checkBoxAdapter.submitList(ArrayList(list)) // Notify adapter that data has changed
    }
    //===============add check box =============================


    // ===================Change the BackGround of the Note===============================

    fun showBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.demo_layout, null)

        val cardViews = listOf<CardView>(
            view.findViewById(R.id.cardView1),
            view.findViewById(R.id.cardView2),
            view.findViewById(R.id.cardView3),
            view.findViewById(R.id.cardView4),
            view.findViewById(R.id.cardView5),
            view.findViewById(R.id.cardView6),
            view.findViewById(R.id.cardView7),
            view.findViewById(R.id.cardView8),
            view.findViewById(R.id.cardView9),
            view.findViewById(R.id.cardView10),
            view.findViewById(R.id.cardView11),
            view.findViewById(R.id.cardView12)
        )

        for (card in cardViews) {
            card.setOnClickListener {
                val colorInt = card.cardBackgroundColor.defaultColor // Get the background color
                color=colorInt
                // Set background immediately
                binding.mainbg.setBackgroundColor(colorInt)
                currentNote.backgroundColor = String.format("#%06X", 0xFFFFFF and colorInt)

                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

}